package com.danum.danum.service.discord;

import com.danum.danum.exception.custom.DiscordException;
import com.danum.danum.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.MessageHistory;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class DiscordServiceImpl implements DiscordService {

    private static final Integer TEXT_DELETED_LIMIT = 99;
    private static final Long MAX_MESSAGE_AGE_DAYS = 14L; // 메시지 최대 나이 (일수 기준)

    private final EndpointLister endpointLister;

    @Value("${discord.channel.api.bot}")
    private String botToken;

    @Value("${discord.channel.api.id}")
    private String channelId; // 채널 ID

    private String currentMessageId; // 현재 메시지 ID

    @EventListener(ApplicationReadyEvent.class)
    @Transactional
    public void sendApiMessage() throws InterruptedException {
        JDA jda = JDABuilder.createDefault(botToken)
                .build()
                .awaitReady();

        TextChannel channel = jda.getTextChannelById(channelId);

        if (channel == null) {
            throw new DiscordException(ErrorCode.DISCORD_CHANNEL_NOT_FOUND);
        }

        // 오래된 메시지 삭제
        deleteOldMessages(channel);

        // 새 메시지 작성
        sendNewMessages(channel);
    }

    private void deleteOldMessages(TextChannel channel) {
        MessageHistory history = new MessageHistory(channel);
        List<Message> messageList;

        do {
            messageList = history.retrievePast(TEXT_DELETED_LIMIT).complete();
            Instant now = Instant.now();
            for (Message message : messageList) {
                Instant messageTime = message.getTimeCreated().toInstant(); // 메시지 생성 시간
                if (messageTime.isBefore(now.minus(MAX_MESSAGE_AGE_DAYS, ChronoUnit.DAYS))) {
                    message.delete().queue();
                }
            }
        } while (messageList.size() == TEXT_DELETED_LIMIT); // 모든 오래된 메시지가 삭제될 때까지 반복
    }

    private void sendNewMessages(TextChannel channel) {
        Map<Class<?>, List<MessageEmbed>> messageEmbedMap = endpointLister.getEndpoints();

        // 기존 메시지 ID가 있다면 삭제하고 새로 작성
        if (currentMessageId != null) {
            channel.deleteMessageById(currentMessageId).queue();
        }

        for (Map.Entry<Class<?>, List<MessageEmbed>> requestMessageMap : messageEmbedMap.entrySet()) {
            channel.sendMessage("## " + requestMessageMap.getKey().getSimpleName())
                    .queue(message -> {
                        currentMessageId = message.getId(); // 현재 메시지 ID 저장
                        sendMessageList(channel, requestMessageMap.getValue());
                    });
        }
    }

    private void sendMessageList(TextChannel channel, List<MessageEmbed> messageEmbedList) {
        if (messageEmbedList.isEmpty()) {
            return;
        }

        int chunk = 10;
        if (messageEmbedList.size() <= chunk) {
            channel.sendMessageEmbeds(messageEmbedList)
                    .queue();
            return;
        }

        channel.sendMessageEmbeds(messageEmbedList.subList(0, chunk))
                .queue(message -> {
                    sendMessageList(channel, messageEmbedList.subList(chunk, messageEmbedList.size()));
                });
    }
}
