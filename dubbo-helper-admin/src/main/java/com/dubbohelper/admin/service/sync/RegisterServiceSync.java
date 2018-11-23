package com.dubbohelper.admin.service.sync;

import com.dubbohelper.admin.config.Config;
import com.dubbohelper.admin.dto.Application;
import com.dubbohelper.admin.dto.Constants;
import com.dubbohelper.admin.dto.URL;
import com.dubbohelper.admin.dto.Version;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.RetryPolicy;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.framework.recipes.cache.TreeCache;
import org.apache.curator.framework.recipes.cache.TreeCacheEvent;
import org.apache.curator.framework.recipes.cache.TreeCacheListener;
import org.apache.curator.retry.ExponentialBackoffRetry;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by zhangxiaoman on 2018/11/14.
 */
@Service
@Slf4j
public class RegisterServiceSync implements InitializingBean, DisposableBean {

    @Autowired
    private Config config;

    private static CuratorFramework client;


    private static TreeCache cache;


    /**
     * 用户收藏列表
     * key:ip
     * value:应用名列表
     */
    public Map<String, List<String>> collectApplications = new HashMap<>();

    /**
     * 应用列表
     * key:应用名
     * value：应用实体
     */
    public ConcurrentMap<String, Application> registryApplicationMap = new ConcurrentHashMap<>();


    @Override
    public void afterPropertiesSet() throws Exception {

        RetryPolicy retryPolicy = new ExponentialBackoffRetry(10000, 3);
        client = CuratorFrameworkFactory.builder()
                .connectString(config.getDubboUrl())
                .retryPolicy(retryPolicy)
                .sessionTimeoutMs(10000)
                .connectionTimeoutMs(10000)
                .build();
        client.start();



        //初始化应用列表
        List<String> serviceList = client.getChildren().forPath(Constants.DUBBO_PATH);
        for (String service : serviceList) {
            String path = new StringBuilder(Constants.DUBBO_PATH).append("/").append(service).append("/").append(Constants.PROVIDER).toString();

            List<String> children = client.getChildren().forPath(path);
            for (String urlStr : children) {
                URL url = URL.valueOf(URL.decode(urlStr));

                String applicationName = url.getParameters().get(Constants.APPLICATION);
                String groupId = url.getParameters().get(Constants.GROUP_ID);
                String artifactId = url.getParameters().get(Constants.ARTIFACT);
                String versionStr = url.getParameters().get(Constants.VERSION);
                if (StringUtils.isEmpty(versionStr)) {
                    versionStr = url.getParameters().get(Constants.APPLICATION_VERSION);
                }
                if (hasEmpty(applicationName, groupId, artifactId, versionStr)) {
                    return;
                }
                String defaultVersion = "";
                if (null != url.getParameters().get(Constants.DEFAULT_VERSION)) {
                    defaultVersion = url.getParameters().get(Constants.DEFAULT_VERSION);
                }
                Application application = registryApplicationMap.get(applicationName);
                if (null == application) {
                    application = Application.builder().application(applicationName).groupId(groupId).artifactId(artifactId).path(path).build();
                    application.setPath(path);
                }
                if (!StringUtils.isEmpty(versionStr)) {
                    Version version = Version.builder().version(versionStr).build();
                    version.getDefaultVersions().add(defaultVersion);
                    application.getVersions().add(version);
                }
                registryApplicationMap.put(applicationName, application);
            }
        }

    }

    /**
     * 添加监听
     *
     * @throws Exception
     */
    private void listener() throws Exception {
        cache = new TreeCache(client, Constants.DUBBO_PATH);
        TreeCacheListener listener1 = (client1, event) -> {
            TreeCacheEvent.Type type = event.getType();
            if (null != event.getData()) {
                String path = URL.decode(event.getData().getPath());
                if (type.equals(TreeCacheEvent.Type.NODE_ADDED)) {
                    //节点添加
                    add((path));
                } else if (type.equals(TreeCacheEvent.Type.NODE_UPDATED)) {
                    //节点更新
                    update(path);
                } else if (type.equals(TreeCacheEvent.Type.NODE_REMOVED)) {
                    //节点删除
                    delete(path);
                }
            }
            log.info("事件类型：{}", event.getType());
            log.info("路径:{}", (null != event.getData() ? URLDecoder.decode(event.getData().getPath()) : null));

        };

        cache.getListenable().addListener(listener1);
        cache.start();
    }

    private void add(String path) {
        if (StringUtils.isEmpty(path) || !path.contains("/providers/")) {
            return;
        }

        int index = path.indexOf("/dubbo:");
        if (index == -1) {
            return;
        }
        String urlPath = path.substring(index);
        URL url = URL.valueOf(urlPath);
        String servicePath = path.substring(0, index);

        String applicationName = url.getParameters().get(Constants.APPLICATION);
        String groupId = url.getParameters().get(Constants.GROUP_ID);
        String artifactId = url.getParameters().get(Constants.ARTIFACT);
        String versionStr = url.getParameters().get(Constants.VERSION);
        if (StringUtils.isEmpty(versionStr)) {
            versionStr = url.getParameters().get(Constants.APPLICATION_VERSION);
        }
        if (hasEmpty(applicationName, groupId, artifactId, versionStr)) {
            return;
        }

        String defaultVersion = "";
        if (null != url.getParameters().get(Constants.DEFAULT_VERSION)) {
            defaultVersion = url.getParameters().get(Constants.DEFAULT_VERSION);
        }
        Application application = registryApplicationMap.get(applicationName);

        if (null == application) {
            application = Application.builder().application(applicationName).groupId(groupId).artifactId(artifactId).path(path).build();
            application.setPath(servicePath);
        }
        if (!StringUtils.isEmpty(versionStr)) {
            Version version = Version.builder().version(versionStr).build();
            version.getDefaultVersions().add(defaultVersion);
            application.getVersions().add(version);
        }
        registryApplicationMap.put(applicationName, application);
    }

    private void update(String path) {
        //TODO
    }

    private void delete(String path) {
        if (StringUtils.isEmpty(path) || !path.contains("/providers/")) {
            return;
        }

        path = path.substring(path.indexOf("dubbo:"));
        URL url = URL.valueOf(path);
        String applicationName = url.getParameters().get(Constants.APPLICATION);
        Application app = registryApplicationMap.get(applicationName);

        if (null == app) {
            return;
        }
        String defaultVersion = "";
        if (null != url.getParameters().get(Constants.DEFAULT_VERSION)) {
            defaultVersion = url.getParameters().get(Constants.DEFAULT_VERSION);
        }

        String versionStr = url.getParameters().get(Constants.VERSION);
        if (StringUtils.isEmpty(versionStr)) {
            versionStr = url.getParameters().get(Constants.APPLICATION_VERSION);
            if (StringUtils.isEmpty(versionStr)) {
                return;
            }
        }

        Iterator<Version> iter = app.getVersions().iterator();
        while (iter.hasNext()) {
            Version version = iter.next();
            if (version.getVersion().equals(versionStr)) {
                List<String> defaultVersions = version.getDefaultVersions();
                Iterator<String> iter2 = defaultVersions.iterator();
                while (iter2.hasNext()) {
                    String defaultVer = iter2.next();
                    if (defaultVer.equals(defaultVersion)) {
                        iter2.remove();
                    }
                }
                if (defaultVersions.size() == 0) {
                    iter.remove();
                }
            }
        }
        if (app.getVersions().size() == 0) {
            registryApplicationMap.remove(applicationName);
        }
    }

    @Override
    public void destroy()  {
        cache.close();
        client.close();
    }

    private boolean hasEmpty(String... strs) {
        boolean b = false;
        for (String str : strs) {
            if (StringUtils.isEmpty(str)) {
                b = true;
            }
        }
        return b;
    }
}
