package kr.co.aim.api.eziframe.router;

import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Component;

@Component
public class MessageRouter implements kr.co.aim.eziframe.handler.MessageRouter {
	
	private final List<String> bpelList = Arrays.asList(
	        "LoadComplete", 
	        "TrackIn", 
	        "TrackOut"
	    );

	@Override
	public boolean isBpel(String messageName) {
		return bpelList.contains(messageName);
	}

}
