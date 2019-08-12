package com.lzl.everything.cmd;

import com.lzl.everything.config.EverythingConfig;
import com.lzl.everything.core.EverythingManager;
import com.lzl.everything.core.model.Condition;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Scanner;

public class MySearchEverythingCmdApplication {

    public static void main(String[] args) {
        //0. Everything Config
        if (args.length >= 1) {
            String configFile = args[0];
            Properties p = new Properties();
            try {
                p.load(new FileInputStream(configFile));
                //p 的值赋值给EverythingConfig对象
                everythingConfigInit(p);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        
        //1. EverythingManager
        EverythingManager manager = EverythingManager.getInstance();
        manager.monitor();

        //2. Scanner
        Scanner scanner = new Scanner(System.in);

        //3. 用户交互输入
        System.out.println("欢迎使用，Everything-everything");
        manager.help();
        while (true) {
            System.out.print(">>");
            String line = scanner.nextLine();
            switch (line) {
                case "help": {
                    manager.help();
                    break;
                }
                case "quit": {
                    manager.quit();
                    break;
                }
                case "index": {
                    manager.buildIndex();
                    break;
                }
                default: {
                    if (line.startsWith("search")) {
                        //解析参数
                        String[] segments = line.split(" ");
                        if (segments.length >= 2) {
                            Condition condition = new Condition();
                            String name = segments[1];
                            condition.setName(name);
                            if (segments.length >= 3) {
                                String type = segments[2];
                                condition.setFileType(type.toUpperCase());
                            }
                            manager.search(condition)
                                    .forEach(thing -> {
                                        System.out.println(thing.getPath());
                                    });
                        } else {
                            manager.help();
                        }
                    } else {
                        manager.help();
                    }
                }

            }
        }
    }

    private static void everythingConfigInit(Properties p) {
        EverythingConfig config = EverythingConfig.getInstance();
        String maxReturn = p.getProperty("everything.max_return");
        String interval = p.getProperty("everything.interval");
        try {
            if (maxReturn != null) {
                config.setMaxReturn(Integer.parseInt(maxReturn));
            }
            if (interval != null) {
                config.setInterval(Integer.parseInt(interval));
            }
        } catch (NumberFormatException e) {

        }
        String enableBuildIndex = p.getProperty("everything.enable_build_index");
        // 如果传进来是非true，这个方法就会返回false
        config.setEnableBuildIndex(Boolean.parseBoolean(enableBuildIndex));

        String orderByDesc = p.getProperty("everything.order_by_desc");
        config.setOrderByDesc(Boolean.parseBoolean(orderByDesc));

        //处理的目录
        String includePaths = p.getProperty("everything.handle_path.include_path");
        if (includePaths != null) {
            String[] paths = includePaths.split(";");
            if (paths.length > 0) {
                //清理到已经有的默认值
                config.getHandlerPath().getIncludePath().clear();
                for (String path : paths) {
                    config.getHandlerPath().addIncludePath(path);
                }
            }
        }

        String excludePaths = p.getProperty("everything.handle_path.exclude_path");
        if (excludePaths != null) {
            String[] paths = excludePaths.split(";");
            if (paths.length > 0) {
                //清理到已经有的默认值
                config.getHandlerPath().getExcludePath().clear();
                for (String path : paths) {
                    config.getHandlerPath().addExcludePath(path);
                }
            }
        }
        System.out.println(config);
    }
}
