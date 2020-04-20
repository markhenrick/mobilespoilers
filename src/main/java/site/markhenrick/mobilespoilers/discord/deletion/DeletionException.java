package site.markhenrick.mobilespoilers.discord.deletion;

public final class DeletionException extends RuntimeException {
	private DeletionException(String message) {
		super(message);
	}

	static DeletionException spoilerNotFound() {
		return new DeletionException("I have no record of that spoiler");
	}

	static DeletionException channelNotFound() {
		return new DeletionException("Sorry, I no longer have access to the channel so cannot delete the spoiler");
	}

	static DeletionException unauthorised() {
		return new DeletionException("Only the user who made that spoiler may delete it");
	}
}
