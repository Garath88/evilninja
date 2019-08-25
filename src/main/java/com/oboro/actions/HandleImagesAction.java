package com.oboro.actions;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.core.entities.Category;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.Message.Attachment;
import net.dv8tion.jda.core.entities.MessageChannel;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import utils.PrivateChannelWrapper;

public final class HandleImagesAction {
    private static final Logger LOGGER = LoggerFactory.getLogger(HandleImagesAction.class);
    private static final long TIME_TO_LIVE_MS = 10000;
    private static SelfExpiringMap<String, String> lowResUsers = new SelfExpiringHashMap<>(TIME_TO_LIVE_MS);
    private static SelfExpiringMap<String, String> sampleUsers = new SelfExpiringHashMap<>(TIME_TO_LIVE_MS);
    private static Message latestLowResMessage = null;
    private static Message latestSampledMessage = null;
    private static final String LOW_RES_SINGLE_IMAGE_MSG = "- You stupid fucking imbecile are you blind?! This picture is made for ants! :ant:\n"
        + "- Remove this shitty ass picture at once, worm!";
    private static final String LOW_RES_MULTIPLE_IMAGE_MSG = "- You stupid fucking imbecile are you blind?! These pictures are made for ants! :ant:\n"
        + "- Remove these shitty ass pictures at once, worm!";
    private static final String SAMPLED_SINGLE_IMAGE_MSG = "- What the hell do you think you are doing?\n"
        + "- Are you an invalid? Read the filename THIS IS A SAMPLED IMAGE you stupid mongrel! :mag:\n"
        + "- Find the actual source image before posting.";
    private static final String SAMPLED_MULTIPLE_IMAGE_MSG = "- What the hell do you think you are doing?\n"
        + "- Are you an invalid? Read the filename THESE ARE SAMPLED IMAGES you stupid mongrel! :mag:\n"
        + "- Find the actual source image before posting.";
    private static final String ANT_EMOJI = "\uD83D\uDC1C";
    private static final String MAGNIFIER_EMOJI = "🔍";
    private static final Pattern IMAGE_PATTERN = Pattern.compile("[^\\s]+(\\.(?i)(jpg|jpeg|png))$");

    private HandleImagesAction() {
    }

    public static void checkImage(MessageReceivedEvent event) {
        Message message = event.getMessage();
        List<Attachment> attachments = message.getAttachments();
        if (!attachments.isEmpty() && !event.getAuthor().isBot()) {
            Category category = event.getTextChannel().getParent();
            if (category != null && category.getName().equalsIgnoreCase("main")) {
                Optional<Attachment> lowResImage = attachments.stream()
                    .filter(HandleImagesAction::isLowResImage)
                    .findFirst();
                if (lowResImage.isPresent()) {
                    sendLowResImageResponse(lowResImage.get(), event);
                    message.addReaction(ANT_EMOJI).queue();
                } else {
                    Optional<Attachment> sampledImage = attachments.stream()
                        .filter(HandleImagesAction::isSampled)
                        .findFirst();
                    sampledImage.ifPresent(attachment -> {
                        sendSampledImageResponse(attachment, event);
                        message.addReaction(MAGNIFIER_EMOJI).queue();
                    });

                }
            }
        }
    }

    private static boolean isLowResImage(Attachment attachment) {
        return isImage(attachment.getFileName())
            && attachment.getHeight() < 500 || attachment.getWidth() < 500;
    }

    private static boolean isImage(String fileName) {
        return IMAGE_PATTERN.matcher(fileName).matches();
    }

    private static boolean isSampled(Attachment attachment) {
        return attachment.getFileName().toLowerCase().contains("sample") || attachment.getFileName().toLowerCase().contains("master");
    }

    private static void sendLowResImageResponse(Attachment image, MessageReceivedEvent event) {
        event.getAuthor().openPrivateChannel().queue(PrivateChannelWrapper.userIsInGuild(pc ->
        {
            String user = event.getAuthor().getAsTag();
            if (!lowResUsers.containsKey(user)) {
                lowResUsers.put(event.getAuthor().getAsTag(), "", TIME_TO_LIVE_MS);
                pc.sendMessage(LOW_RES_SINGLE_IMAGE_MSG).queue(success -> {
                    latestLowResMessage = success;
                    sendAttachmentToChannelAndAddReaction(Collections.singletonList(image), pc, ANT_EMOJI);
                }, fail -> {
                });
            } else {
                if (latestLowResMessage != null) {
                    latestLowResMessage.editMessage(LOW_RES_MULTIPLE_IMAGE_MSG).queue();
                }
                sendAttachmentToChannelAndAddReaction(Collections.singletonList(image), pc, ANT_EMOJI);
            }
        }));
    }

    private static void sendSampledImageResponse(Attachment image, MessageReceivedEvent event) {
        event.getAuthor().openPrivateChannel().queue(PrivateChannelWrapper.userIsInGuild(pc ->
        {
            String user = event.getAuthor().getAsTag();
            if (!sampleUsers.containsKey(user)) {
                sampleUsers.put(event.getAuthor().getAsTag(), "", TIME_TO_LIVE_MS);
                pc.sendMessage(SAMPLED_SINGLE_IMAGE_MSG).queue(success -> {
                    latestSampledMessage = success;
                    sendAttachmentToChannelAndAddReaction(Collections.singletonList(image), pc, MAGNIFIER_EMOJI);
                }, fail -> {
                });
            } else {
                if (latestSampledMessage != null) {
                    latestSampledMessage.editMessage(SAMPLED_MULTIPLE_IMAGE_MSG).queue();
                }
                sendAttachmentToChannelAndAddReaction(Collections.singletonList(image), pc, MAGNIFIER_EMOJI);
            }
        }));
    }

    private static void sendAttachmentToChannelAndAddReaction(List<Attachment> attachments, MessageChannel channel, String imageReaction) {
        attachments.forEach(attachment -> {
            try {
                channel.sendFile(attachment.getInputStream(), attachment.getFileName())
                    .queue(success -> success.addReaction(imageReaction).queue());
            } catch (IOException e) {
                LOGGER.error("Failed to send attachment to user in DM", e);
            }
        });
    }
}
