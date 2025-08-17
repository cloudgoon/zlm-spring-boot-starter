package io.github.lunasaw.zlm.hook.param;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * zlm hook事件中的on_shell_login事件的参数
 * shell登录鉴权，ZLMediaKit提供简单的telnet调试方式
 *
 * @author luna
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OnShellLoginHookParam extends HookParam {

    /**
     * TCP链接唯一ID
     */
    private String id;

    /**
     * telnet 终端ip
     */
    private String ip;

    /**
     * telnet 终端登录用户密码
     */
    private String passwd;

    /**
     * telnet 终端端口号
     */
    private int port;

    /**
     * telnet 终端登录用户名
     */
    private String userName;

}