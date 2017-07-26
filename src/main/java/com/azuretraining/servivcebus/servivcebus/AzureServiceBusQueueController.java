package com.azuretraining.servivcebus.servivcebus;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.microsoft.windowsazure.Configuration;
import com.microsoft.windowsazure.exception.ServiceException;
import com.microsoft.windowsazure.services.servicebus.ServiceBusConfiguration;
import com.microsoft.windowsazure.services.servicebus.ServiceBusContract;
import com.microsoft.windowsazure.services.servicebus.ServiceBusService;
import com.microsoft.windowsazure.services.servicebus.models.BrokeredMessage;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMessageOptions;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveMode;
import com.microsoft.windowsazure.services.servicebus.models.ReceiveQueueMessageResult;

@RestController
public class AzureServiceBusQueueController {

	private static ServiceBusContract service;

	static {
		// Create a queue
		Configuration config = ServiceBusConfiguration.configureWithSASAuthentication("rjqueue",
				"aruneshservicebuskey", "pRyrdDQd9rywyq7/N1E7L/tDJ6nuKfjqNv+c5tuUV88=", ".servicebus.windows.net");

		service = ServiceBusService.create(config);
	}

	@RequestMapping("/add/{message}")
	public String sendMessageToQueue(@PathVariable("message") String message) {
		try {

			service.getQueue("aruneshqueue");
			BrokeredMessage brokeredMessage = new BrokeredMessage(message);
			service.sendQueueMessage("aruneshqueue", brokeredMessage);
			
		} catch (ServiceException e) {
			e.printStackTrace();
		}
		return "Message send to Queue";
	}

	@RequestMapping("/read")
	public String receiveMessageFromQueue() {
		try {
			ReceiveMessageOptions opts = ReceiveMessageOptions.DEFAULT;
			opts.setReceiveMode(ReceiveMode.PEEK_LOCK);

			while (true) {
				ReceiveQueueMessageResult resultQM = service.receiveQueueMessage("aruneshqueue", opts);
				BrokeredMessage message = resultQM.getValue();
				System.out.println(message);
				if (message != null && message.getMessageId() != null) {
					System.out.println("MessageID: " + message.getMessageId());
					// Display the queue message.
					System.out.print("From queue: ");
					byte[] b = new byte[200];
					String s = null;
					int numRead = message.getBody().read(b);
					while (-1 != numRead) {
						s = new String(b);
						s = s.trim();
						System.out.print("s = " + s);
						numRead = message.getBody().read(b);
					}
					System.out.println("Deleting the message after reading it.");
					service.deleteMessage(message);
				} else {
					System.out.println("No more messages.");
					break;
				}
			}
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
