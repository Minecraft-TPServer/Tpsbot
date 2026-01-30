package top.Future.tps.utils;

import net.minecraft.text.Text;
import net.minecraft.server.command.CommandOutput;

import java.util.ArrayList;
import java.util.List;

// 自定义 CommandOutput 实现
public class CommandResultOutput implements CommandOutput {
    private final List<Text> messages = new ArrayList<>();

    @Override
    public void sendMessage(Text message) {
        messages.add(message);
    }

    @Override
    public boolean shouldReceiveFeedback() {
        return true;  // 设置为 true 以接收反馈
    }

    @Override
    public boolean shouldTrackOutput() {
        return true;  // 设置为 true 以跟踪输出
    }

    @Override
    public boolean shouldBroadcastConsoleToOps() {
        return false;  // 不向 OP 广播
    }

    // 获取所有消息
    public List<Text> getMessages() {
        return new ArrayList<>(messages);
    }

    // 获取消息的字符串形式
    public List<String> getMessageStrings() {
        List<String> result = new ArrayList<>();
        for (Text text : messages) {
            result.add(text.getString());
        }
        return result;
    }

    // 获取单条组合消息
    public String getCombinedMessage() {
        StringBuilder sb = new StringBuilder();
        for (Text text : messages) {
            sb.append(text.getString()).append("\n");
        }
        return sb.toString().trim();
    }

    // 清空消息
    public void clearMessages() {
        messages.clear();
    }
}