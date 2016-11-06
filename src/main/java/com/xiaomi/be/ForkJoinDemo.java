package com.xiaomi.be;


import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.RecursiveTask;

public class ForkJoinDemo {


    static class FileCountTask extends RecursiveTask {
        private Path dir;

        public FileCountTask(Path dir) {
            this.dir = dir;
        }

        @Override
        protected Object compute() {
            int count = 0;
            List<FileCountTask> subTasks = new ArrayList<FileCountTask>();

            try (DirectoryStream<Path> ds = Files.newDirectoryStream(dir)) {
                for (Path subPath : ds) {
                    if (Files.isDirectory(subPath, LinkOption.NOFOLLOW_LINKS)) {
                        // 对每个子目录都新建一个子任务。
                        subTasks.add(new FileCountTask(subPath));
                    } else {
                        // 遇到文件，则计数器增加 1。
                        count++;
                    }
                }

                if (!subTasks.isEmpty()) {
                    // 在当前的 ForkJoinPool 上调度所有的子任务。
                    for (FileCountTask subTask : invokeAll(subTasks)) {
                        count += (Integer) subTask.join();
                    }
                }
            } catch (IOException e) {
                return 0;
            }
            return count;
        }
    }

    public static void main(String[] args) {
        long start = System.currentTimeMillis();
        String path = "/home/tsx";
        Integer count = (Integer) new ForkJoinPool().invoke(new FileCountTask(Paths.get("/home/tsx")));
        System.out.println(path + " file count " + count + " time cost:" + (System.currentTimeMillis() - start));
    }
}
