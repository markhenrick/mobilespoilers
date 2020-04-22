package site.markhenrick.mobilespoilers.dal;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@ToString
public class Spoiler {
	@Id
	@Getter
	@Setter
	private Long messageId;

	@Getter
	@Setter
	private Long channelId;

	@Getter
	@Setter
	private Long userId;
}
