package server.commands;

class ChunkData {

    private final int id;
    private final long pausedAt;

    ChunkData(int id, long pausedAt) {
        this.id = id;
        this.pausedAt = pausedAt;
    }

    int getId() {
        return id;
    }

    long getPausedAt() {
        return pausedAt;
    }

}
