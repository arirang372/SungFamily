package com.sungfamily.events;

import com.sungfamily.models.ChatMessage;

public class ChatMessageReceivedEvent
{
    private ChatMessage message;
    public ChatMessageReceivedEvent(ChatMessage message)
    {
        this.message = message;
    }

    public ChatMessage getChatMessage()
    {
        return this.message;
    }
}
