package com.community.tool;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class CreateProject {

    // 定义项目结构
    static class ProjectNode {
        String path;
        boolean isDirectory;
        String content;

        ProjectNode(String path, boolean isDirectory, String content) {
            this.path = path;
            this.isDirectory = isDirectory;
            this.content = content;
        }
    }

    public static void main(String[] args) {
        try {
            createFrontendProject();
            System.out.println("项目创建完成！");
        } catch (IOException e) {
            System.err.println("创建项目时出错: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void createFrontendProject() throws IOException {
        String baseDir = "frontend";

        // 定义项目结构
        List<ProjectNode> projectStructure = new ArrayList<>();

        // 创建目录
        projectStructure.add(new ProjectNode("public", true, null));
        projectStructure.add(new ProjectNode("public/favicon.ico", false, ""));
        projectStructure.add(new ProjectNode("public/index.html", false, getIndexHtml()));

        projectStructure.add(new ProjectNode("src", true, null));
        projectStructure.add(new ProjectNode("src/api", true, null));
        projectStructure.add(new ProjectNode("src/api/types", true, null));
        projectStructure.add(new ProjectNode("src/api/modules", true, null));
        projectStructure.add(new ProjectNode("src/api/index.ts", false, "// API统一导出文件"));
        projectStructure.add(new ProjectNode("src/api/axios.ts", false, "// Axios实例配置"));
        projectStructure.add(new ProjectNode("src/api/modules/user.ts", false, "// 用户相关API"));
        projectStructure.add(new ProjectNode("src/api/modules/system.ts", false, "// 系统相关API"));

        projectStructure.add(new ProjectNode("src/assets", true, null));
        projectStructure.add(new ProjectNode("src/assets/images", true, null));
        projectStructure.add(new ProjectNode("src/assets/icons", true, null));
        projectStructure.add(new ProjectNode("src/assets/styles", true, null));
        projectStructure.add(new ProjectNode("src/assets/styles/variables.scss", false, "// SCSS变量"));
        projectStructure.add(new ProjectNode("src/assets/styles/index.scss", false, "// 全局样式"));

        projectStructure.add(new ProjectNode("src/components", true, null));
        projectStructure.add(new ProjectNode("src/components/common", true, null));
        projectStructure.add(new ProjectNode("src/components/common/Layout", true, null));
        projectStructure.add(new ProjectNode("src/components/common/Table", true, null));
        projectStructure.add(new ProjectNode("src/components/common/Form", true, null));
        projectStructure.add(new ProjectNode("src/components/business", true, null));

        projectStructure.add(new ProjectNode("src/composables", true, null));
        projectStructure.add(new ProjectNode("src/composables/usePagination.ts", false, "// 分页组合式函数"));
        projectStructure.add(new ProjectNode("src/composables/useForm.ts", false, "// 表单组合式函数"));
        projectStructure.add(new ProjectNode("src/composables/usePermission.ts", false, "// 权限组合式函数"));

        projectStructure.add(new ProjectNode("src/directives", true, null));
        projectStructure.add(new ProjectNode("src/directives/permission.ts", false, "// 权限指令"));
        projectStructure.add(new ProjectNode("src/directives/lazyLoad.ts", false, "// 懒加载指令"));

        projectStructure.add(new ProjectNode("src/layouts", true, null));
        projectStructure.add(new ProjectNode("src/layouts/DefaultLayout.vue", false, "// 默认布局组件"));
        projectStructure.add(new ProjectNode("src/layouts/AuthLayout.vue", false, "// 认证布局组件"));

        projectStructure.add(new ProjectNode("src/router", true, null));
        projectStructure.add(new ProjectNode("src/router/routes", true, null));
        projectStructure.add(new ProjectNode("src/router/routes/modules", true, null));
        projectStructure.add(new ProjectNode("src/router/guards", true, null));
        projectStructure.add(new ProjectNode("src/router/index.ts", false, "// 路由配置入口"));
        projectStructure.add(new ProjectNode("src/router/routes/index.ts", false, "// 路由定义"));
        projectStructure.add(new ProjectNode("src/router/routes/modules/dashboard.ts", false, "// 仪表板路由"));
        projectStructure.add(new ProjectNode("src/router/routes/modules/system.ts", false, "// 系统路由"));

        projectStructure.add(new ProjectNode("src/stores", true, null));
        projectStructure.add(new ProjectNode("src/stores/modules", true, null));
        projectStructure.add(new ProjectNode("src/stores/index.ts", false, "// Pinia store入口"));
        projectStructure.add(new ProjectNode("src/stores/modules/user.ts", false, "// 用户store"));
        projectStructure.add(new ProjectNode("src/stores/modules/app.ts", false, "// 应用store"));
        projectStructure.add(new ProjectNode("src/stores/modules/permission.ts", false, "// 权限store"));

        projectStructure.add(new ProjectNode("src/types", true, null));
        projectStructure.add(new ProjectNode("src/types/modules", true, null));
        projectStructure.add(new ProjectNode("src/types/api.d.ts", false, "// API类型定义"));
        projectStructure.add(new ProjectNode("src/types/global.d.ts", false, "// 全局类型定义"));

        projectStructure.add(new ProjectNode("src/utils", true, null));
        projectStructure.add(new ProjectNode("src/utils/auth.ts", false, "// 认证工具函数"));
        projectStructure.add(new ProjectNode("src/utils/request.ts", false, "// 请求工具函数"));
        projectStructure.add(new ProjectNode("src/utils/validate.ts", false, "// 验证工具函数"));
        projectStructure.add(new ProjectNode("src/utils/index.ts", false, "// 工具函数统一导出"));

        projectStructure.add(new ProjectNode("src/views", true, null));
        projectStructure.add(new ProjectNode("src/views/modules", true, null));
        projectStructure.add(new ProjectNode("src/views/modules/user", true, null));
        projectStructure.add(new ProjectNode("src/views/modules/system", true, null));
        projectStructure.add(new ProjectNode("src/views/Login.vue", false, "// 登录页面"));
        projectStructure.add(new ProjectNode("src/views/Dashboard.vue", false, "// 仪表板页面"));
        projectStructure.add(new ProjectNode("src/views/modules/user/UserList.vue", false, "// 用户列表页面"));
        projectStructure.add(new ProjectNode("src/views/modules/user/UserDetail.vue", false, "// 用户详情页面"));

        projectStructure.add(new ProjectNode("src/App.vue", false, getAppVue()));
        projectStructure.add(new ProjectNode("src/main.ts", false, getMainTs()));

        projectStructure.add(new ProjectNode(".env.development", false, "# 开发环境变量"));
        projectStructure.add(new ProjectNode(".env.production", false, "# 生产环境变量"));
        projectStructure.add(new ProjectNode(".env.test", false, "# 测试环境变量"));
        projectStructure.add(new ProjectNode("vite.config.ts", false, getViteConfig()));
        projectStructure.add(new ProjectNode("tsconfig.json", false, getTsConfig()));
        projectStructure.add(new ProjectNode("package.json", false, getPackageJson()));
        projectStructure.add(new ProjectNode("README.md", false, "# 前端项目\n\n这是一个基于Vue 3 + TypeScript + Vite的项目。"));

        // 检查目录是否存在
        File projectDir = new File(baseDir);
        if (projectDir.exists()) {
            System.out.println("frontend目录已存在，是否删除? (y/n)");
            int response = System.in.read();
            if (response == 'y' || response == 'Y') {
                deleteDirectory(projectDir);
            } else {
                System.out.println("操作已取消");
                return;
            }
        }

        // 创建目录和文件
        for (ProjectNode node : projectStructure) {
            String fullPath = baseDir + "/" + node.path;
            File file = new File(fullPath);

            if (node.isDirectory) {
                if (file.mkdirs()) {
                    System.out.println("创建目录: " + fullPath);
                }
            } else {
                if (file.getParentFile() != null && !file.getParentFile().exists()) {
                    file.getParentFile().mkdirs();
                }
                try (FileWriter writer = new FileWriter(file)) {
                    writer.write(node.content != null ? node.content : "");
                    System.out.println("创建文件: " + fullPath);
                }
            }
        }
    }

    // 删除目录
    private static void deleteDirectory(File dir) {
        if (dir.isDirectory()) {
            File[] children = dir.listFiles();
            if (children != null) {
                for (File child : children) {
                    deleteDirectory(child);
                }
            }
        }
        dir.delete();
    }

    // 配置文件内容
    private static String getPackageJson() {
        return "{\n" +
                "  \"name\": \"frontend-project\",\n" +
                "  \"version\": \"1.0.0\",\n" +
                "  \"private\": true,\n" +
                "  \"scripts\": {\n" +
                "    \"dev\": \"vite\",\n" +
                "    \"build\": \"vue-tsc && vite build\",\n" +
                "    \"preview\": \"vite preview\"\n" +
                "  },\n" +
                "  \"dependencies\": {\n" +
                "    \"vue\": \"^3.3.0\",\n" +
                "    \"pinia\": \"^2.1.0\",\n" +
                "    \"vue-router\": \"^4.2.0\",\n" +
                "    \"axios\": \"^1.4.0\"\n" +
                "  },\n" +
                "  \"devDependencies\": {\n" +
                "    \"@types/node\": \"^20.0.0\",\n" +
                "    \"@vitejs/plugin-vue\": \"^4.2.0\",\n" +
                "    \"typescript\": \"^5.0.0\",\n" +
                "    \"vite\": \"^4.3.0\",\n" +
                "    \"vue-tsc\": \"^1.4.0\",\n" +
                "    \"sass\": \"^1.62.0\"\n" +
                "  }\n" +
                "}";
    }

    private static String getViteConfig() {
        return "import { defineConfig } from 'vite'\n" +
                "import vue from '@vitejs/plugin-vue'\n" +
                "import path from 'path'\n" +
                "\n" +
                "export default defineConfig({\n" +
                "  plugins: [vue()],\n" +
                "  resolve: {\n" +
                "    alias: {\n" +
                "      '@': path.resolve(__dirname, './src')\n" +
                "    }\n" +
                "  }\n" +
                "})";
    }

    private static String getTsConfig() {
        return "{\n" +
                "  \"compilerOptions\": {\n" +
                "    \"target\": \"ES2020\",\n" +
                "    \"useDefineForClassFields\": true,\n" +
                "    \"lib\": [\"ES2020\", \"DOM\", \"DOM.Iterable\"],\n" +
                "    \"module\": \"ESNext\",\n" +
                "    \"skipLibCheck\": true,\n" +
                "    \"baseUrl\": \".\",\n" +
                "    \"paths\": {\n" +
                "      \"@/*\": [\"src/*\"]\n" +
                "    },\n" +
                "    \"moduleResolution\": \"node\",\n" +
                "    \"allowImportingTsExtensions\": true,\n" +
                "    \"resolveJsonModule\": true,\n" +
                "    \"isolatedModules\": true,\n" +
                "    \"noEmit\": true,\n" +
                "    \"jsx\": \"preserve\",\n" +
                "    \"strict\": true\n" +
                "  },\n" +
                "  \"include\": [\"src/**/*.ts\", \"src/**/*.d.ts\", \"src/**/*.tsx\", \"src/**/*.vue\"]\n" +
                "}";
    }

    private static String getIndexHtml() {
        return "<!DOCTYPE html>\n" +
                "<html lang=\"zh-CN\">\n" +
                "  <head>\n" +
                "    <meta charset=\"UTF-8\" />\n" +
                "    <link rel=\"icon\" type=\"image/svg+xml\" href=\"/vite.svg\" />\n" +
                "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
                "    <title>Vue 3 + TypeScript + Vite</title>\n" +
                "  </head>\n" +
                "  <body>\n" +
                "    <div id=\"app\"></div>\n" +
                "    <script type=\"module\" src=\"/src/main.ts\"></script>\n" +
                "  </body>\n" +
                "</html>";
    }

    private static String getMainTs() {
        return "import { createApp } from 'vue'\n" +
                "import { createPinia } from 'pinia'\n" +
                "import App from './App.vue'\n" +
                "import router from './router'\n" +
                "\n" +
                "const app = createApp(App)\n" +
                "\n" +
                "app.use(createPinia())\n" +
                "app.use(router)\n" +
                "\n" +
                "app.mount('#app')";
    }

    private static String getAppVue() {
        return "<template>\n" +
                "  <router-view />\n" +
                "</template>\n" +
                "\n" +
                "<script setup lang=\"ts\">\n" +
                "// 根组件\n" +
                "</script>\n" +
                "\n" +
                "<style>\n" +
                "#app {\n" +
                "  width: 100%;\n" +
                "  height: 100vh;\n" +
                "}\n" +
                "</style>";
    }
}