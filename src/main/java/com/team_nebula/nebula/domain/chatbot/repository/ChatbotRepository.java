package com.team_nebula.nebula.domain.chatbot.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.team_nebula.nebula.domain.chatbot.entity.ChatBot;

import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class ChatbotRepository {

	private final DynamoDbTable<ChatBot> chatBotTable;

	public ChatbotRepository(@Autowired DynamoDbEnhancedClient dynamoDbEnhancedClient) {
		this.chatBotTable = dynamoDbEnhancedClient.table("chatbot", TableSchema.fromBean(ChatBot.class));
	}

	public void saveChat(ChatBot chatBot) {
		chatBotTable.putItem(chatBot);
	}

	public ChatBot getChatById(String chatId) {
		return chatBotTable.getItem(Key.builder().partitionValue(chatId).build());
	}

	public void updateChat(ChatBot chatBot) {
		chatBotTable.updateItem(chatBot);
	}

	public void deleteChat(String chatId) {
		chatBotTable.deleteItem(Key.builder().partitionValue(chatId).build());
	}
}