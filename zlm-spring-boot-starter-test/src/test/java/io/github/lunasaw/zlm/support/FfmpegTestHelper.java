package io.github.lunasaw.zlm.support;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.function.BooleanSupplier;

/**
 * E2E 推拉流测试基础设施：封装 ffmpeg 子进程的启动/停止与轮询断言。
 * 仅供测试使用，不进入主库。
 *
 * @author luna
 * @date 2026/06/10
 */
public final class FfmpegTestHelper {

    private FfmpegTestHelper() {
    }

    /**
     * 检测 ffmpeg 是否可用（exec -version）。
     */
    public static boolean isFfmpegAvailable(String ffmpeg) {
        if (ffmpeg == null || ffmpeg.isEmpty()) {
            return false;
        }
        try {
            Process p = new ProcessBuilder(ffmpeg, "-version")
                    .redirectErrorStream(true)
                    .start();
            boolean done = p.waitFor(5, TimeUnit.SECONDS);
            if (!done) {
                p.destroyForcibly();
                return false;
            }
            return p.exitValue() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * RTSP 推流（-c copy，循环推送本地文件）。
     */
    public static Process pushRtsp(String ffmpeg, String file, String url) {
        return push(ffmpeg, buildArgs(ffmpeg, file, null, "rtsp", url));
    }

    /**
     * RTMP 推流（-c copy，循环推送本地文件，flv 封装）。
     */
    public static Process pushRtmp(String ffmpeg, String file, String url) {
        return push(ffmpeg, buildArgs(ffmpeg, file, null, "flv", url));
    }

    /**
     * 裸 H264 推流（需指定帧率）。
     */
    public static Process pushH264Rtsp(String ffmpeg, String file, String url, int fps) {
        return push(ffmpeg, buildArgs(ffmpeg, file, fps, "rtsp", url));
    }

    private static List<String> buildArgs(String ffmpeg, String file, Integer fps, String format, String url) {
        List<String> args = new ArrayList<>();
        args.add(ffmpeg);
        args.add("-re");
        args.add("-stream_loop");
        args.add("-1");
        if (fps != null) {
            args.add("-r");
            args.add(String.valueOf(fps));
        }
        args.add("-i");
        args.add(file);
        args.add("-c");
        args.add("copy");
        args.add("-f");
        args.add(format);
        if ("rtsp".equals(format)) {
            args.add("-rtsp_transport");
            args.add("tcp");
        }
        args.add(url);
        return args;
    }

    private static Process push(String ffmpeg, List<String> args) {
        try {
            return new ProcessBuilder(args)
                    .redirectErrorStream(true)
                    .redirectOutput(ProcessBuilder.Redirect.DISCARD)
                    .start();
        } catch (Exception e) {
            throw new RuntimeException("启动 ffmpeg 推流失败: " + e.getMessage(), e);
        }
    }

    /**
     * 强制销毁 ffmpeg 子进程。
     */
    public static void stop(Process p) {
        if (p != null && p.isAlive()) {
            p.destroyForcibly();
            try {
                p.waitFor(3, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * 轮询等待条件成立：每 intervalMs 检查一次，最多等待 timeoutSec 秒。
     *
     * @return 条件是否在超时前成立
     */
    public static boolean awaitTrue(BooleanSupplier condition, int timeoutSec, long intervalMs) {
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(timeoutSec);
        while (System.nanoTime() < deadline) {
            try {
                if (condition.getAsBoolean()) {
                    return true;
                }
            } catch (Exception ignored) {
                // 轮询期间的瞬时异常忽略，继续重试
            }
            try {
                Thread.sleep(intervalMs);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return false;
            }
        }
        return false;
    }

    /**
     * 解析测试素材文件，相对路径基于测试模块工作目录。
     */
    public static File resolveFile(String filesDir, String name) {
        return new File(filesDir, name);
    }
}
