package site.markhenrick.mobilespoilers.dal;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
@Data
public class Spoiler {
	@Id
	private Long messageId;
	private Long channelId;
	private Long userId;
}
