package com.dubbohelper.admin.util;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.runtime.RuntimeConstants;
import org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader;

import java.io.IOException;
import java.io.StringWriter;

/**
 * 模板文件处理
 *
 * @author lijinbo
 */
public class VelocityUtil {

    public static String createApiDoc(VelocityContext ctx,String templateName) throws IOException {
        // 初始化模板引擎
        VelocityEngine ve = new VelocityEngine();
        ve.setProperty(RuntimeConstants.RESOURCE_LOADER, "classpath");
        ve.setProperty("classpath.resource.loader.class", ClasspathResourceLoader.class.getName());
        ve.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
        ve.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
        ve.init();
        // 获取模板文件
        Template template = ve.getTemplate(templateName);
        // 输出
        StringWriter sw = new StringWriter();
        template.merge(ctx,sw);

        return sw.toString();
    }
}
