package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.common.enums.FilePathEnum;
import com.dubbohelper.admin.common.util.FileUtil;
import com.dubbohelper.admin.common.util.MavenUtil;
import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.dto.MavenDataDTO;
import com.dubbohelper.admin.service.ConfigureService;
import com.dubbohelper.admin.service.JarService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.version.Version;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class JarServiceImpl implements JarService {

    private static Map<String, MavenCoordDTO> applicationInfos = Maps.newConcurrentMap();

    private static Map<String, MavenCoordDTO> jarInfos = Maps.newConcurrentMap();

    @Autowired
    private ConfigureService configureService;

    @Override
    public Set<MavenCoordDTO> search(String artifactId) {
        Set<MavenCoordDTO> applications = new HashSet<>();

        if (applicationInfos.isEmpty()) {
            loadJarInfoFile();
        }
        Set<String> list = applicationInfos.keySet();

        if (CollectionUtils.isNotEmpty(list)) {
            for (String application : list) {
                if (application.contains(artifactId)) {
                    MavenCoordDTO dto = applicationInfos.get(application);
                    if (dto.getArtifactId().contains(artifactId)) {
                        MavenCoordDTO mavenCoordDTO = MavenCoordDTO.builder()
                                .applicationName(dto.getApplicationName())
                                .groupId(dto.getGroupId())
                                .artifactId(dto.getArtifactId())
                                .build();
                        applications.add(mavenCoordDTO);
                    }
                }
            }
        }

        return applications;
    }

    @Override
    public boolean insertOrUpdateJar(MavenCoordDTO dto) {
        MavenDataDTO mavenDataDTO = new MavenDataDTO(dto);
        try {
            mavenDataDTO.setRepository(configureService.getConfigures().getRepositoryPath());
            MavenUtil.downLoad(mavenDataDTO);
        } catch (ArtifactResolutionException e) {
            log.error("拉取jar包失败", e);
            return false;
        } catch (Exception e) {
            log.error("拉取jar包异常", e);
            return false;
        }

        if (jarInfos.isEmpty()) {
            loadJarInfoFile();
        }
        String applicationInfoKey = dto.getGroupId() + "." + dto.getArtifactId();
        String jarInfoKey = applicationInfoKey + "." + dto.getVersion();
        if (StringUtils.isEmpty(jarInfos.get(jarInfoKey))) {
            String applicationInfo = dto.getApplicationName() + "|" +dto.getGroupId() + "|" + dto.getArtifactId() + "|" + dto.getVersion()
                    + "|" + new Date().toString() + "\n";
            if (FileUtil.appendContent(FilePathEnum.JARINFOS.getAbsolutePath(), applicationInfo)) {
                applicationInfos.put(applicationInfoKey, dto);
                jarInfos.put(jarInfoKey, dto);
            } else {
                return false;
            }
        }

        return true;
    }

    @Override
    public List<String> getJarVersions(MavenCoordDTO dto) {
        List<String> result = new ArrayList<>();

        MavenDataDTO mavenDataDTO = new MavenDataDTO(dto);
        mavenDataDTO.setRepository(configureService.getConfigures().getRepositoryPath());
        try {
            List<Version> versions = MavenUtil.getAllVersions(mavenDataDTO);
            if (CollectionUtils.isNotEmpty(versions)) {
                for (Version version : versions) {
                    result.add(version.toString());
                }
            }
        } catch (Exception e) {
            log.error("获取Jar在仓库中的所有版本失败", e);
        }
        return result;
    }

    /**
     * 加载文件内容并缓存
     */
    private void loadJarInfoFile() {
        String template = "applicationName|groupId|artifactId|version|date";
        String[] fields = template.split("[|]");

        List<String> jarInfoList = FileUtil.readFileByLine(FilePathEnum.JARINFOS.getAbsolutePath());
        if (CollectionUtils.isNotEmpty(jarInfoList)) {
            for (String jarInfo : jarInfoList) {
                String[] values = jarInfo.split("[|]");
                if (fields.length != values.length) {
                    log.error("记录数据和模板数据无法匹配:{}", "");
                    continue;
                }

                MavenCoordDTO mavenCoordDTO = MavenCoordDTO.builder()
                        .applicationName(values[0])
                        .groupId(values[1])
                        .artifactId(values[2])
                        .version(values[3])
                        .build();
                String applicationInfoKey = values[1] + "." + values[2];
                String jarInfoKey = applicationInfoKey + "." + values[3];
                applicationInfos.put(applicationInfoKey, mavenCoordDTO);
                jarInfos.put(jarInfoKey, mavenCoordDTO);
            }
        }
    }
}
