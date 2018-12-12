package com.dubbohelper.admin.service.impl;

import com.dubbohelper.admin.common.enums.FilePathEnum;
import com.dubbohelper.admin.common.util.FileUtil;
import com.dubbohelper.admin.common.util.MavenUtil;
import com.dubbohelper.admin.dto.MavenCoordDTO;
import com.dubbohelper.admin.dto.MavenDataDTO;
import com.dubbohelper.admin.service.JarService;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Slf4j
@Service
public class JarServiceImpl implements JarService {

    private static Map<String, MavenCoordDTO> applicationInfos = Maps.newConcurrentMap();

    private static Map<String, MavenCoordDTO> jarInfos = Maps.newConcurrentMap();

    @Override
    public Map<String, MavenCoordDTO> searchApplication(String artifactId) {
        Map<String, MavenCoordDTO> applications = new HashMap<>();

        if (applicationInfos.isEmpty()) {
            loadJarInfoFile();
        }
        Set<String> list = applicationInfos.keySet();

        if (CollectionUtils.isNotEmpty(list)) {
            for (String application : list) {
                if (application.contains(artifactId)) {
                    MavenCoordDTO dto = applicationInfos.get(application);
                    if (dto.getArtifactId().contains(artifactId)) {
                        applications.put(application, dto);
                    }
                }
            }
        }

        return applications;
    }

    @Override
    public List<MavenCoordDTO> getJars(String groupId, String artifactId) {
        List<MavenCoordDTO> mavenCoordDTOS = new ArrayList<>();

        if (jarInfos.isEmpty()) {
            loadJarInfoFile();
        }
        Set<String> list = jarInfos.keySet();

        if (CollectionUtils.isNotEmpty(list)) {
            for (String jarInfo : list) {
                if (jarInfo.contains(groupId + "." + artifactId)) {
                    mavenCoordDTOS.add(jarInfos.get(jarInfo));
                }
            }
        }

        return mavenCoordDTOS;
    }

    @Override
    public boolean insertOrUpdateJar(MavenCoordDTO dto) {
        MavenDataDTO mavenDataDTO = new MavenDataDTO(dto);
        try {
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
            String applicationInfo = dto.getGroupId() + "|" + dto.getArtifactId() + "|" + dto.getVersion()
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

    /**
     * 加载文件内容并缓存
     */
    private void loadJarInfoFile() {
        String template = "groupId|artifactId|version|date";
        String[] fields = template.split("[|]");

        List<String> jarInfoList = FileUtil.readFileByLine(FilePathEnum.JARINFOS.getAbsolutePath());
        if (CollectionUtils.isNotEmpty(jarInfoList)) {
            for (String jarInfo : jarInfoList) {
                String[] values = jarInfo.split("[|]");
                if (fields.length != values.length) {
                    log.error("记录数据和模板数据无法匹配:{}", "");
                    continue;
                }

                MavenCoordDTO mavenCoordDTO = new MavenCoordDTO();
                mavenCoordDTO.setGroupId(values[0]);
                mavenCoordDTO.setArtifactId(values[1]);
                mavenCoordDTO.setVersion(values[2]);
                String applicationInfoKey = values[0] + "." + values[1];
                String jarInfoKey = applicationInfoKey + "." + values[2];
                applicationInfos.put(applicationInfoKey, mavenCoordDTO);
                jarInfos.put(jarInfoKey, mavenCoordDTO);
            }
        }
    }
}
