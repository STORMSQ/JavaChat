package tw.com.stormsq.model;


public class Info {

	private User from;
	private User to;
	private String content;
	private SendType type;
	private String timestamp;
	public User getFrom() {
		return from;
	}
	public void setFrom(User from) {
		this.from = from;
	}
	public User getTo() {
		return to;
	}
	public void setTo(User to) {
		this.to = to;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public SendType getType() {
		return type;
	}
	public void setType(SendType type) {
		this.type = type;
	}
	@Override
	public String toString() {
		return "Info [from=" + from + ", to=" + to + ", content=" + content + ", type=" + type + ", timestamp="
				+ timestamp + "]";
	}
	public String getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}


	
	
}


