package de.likewhat.customheads.headwriter;

import java.util.*;

public class HistoryManager {

    private static final Map<UUID, LinkedList<BlockHistoryCollection>> blockHistoryMap = new HashMap<>();

    public static Optional<LinkedList<BlockHistoryCollection>> getBlockHistory(UUID uuid) {
        return Optional.ofNullable(blockHistoryMap.get(uuid));
    }

    public static void addBlockHistory(UUID uuid, BlockHistoryCollection blockHistory) {
        LinkedList<BlockHistoryCollection> historyList = getBlockHistory(uuid).orElse(new LinkedList<>());
        if(!blockHistory.isFinished()) {
            blockHistory.finish(); // Close History if not already done to prevent later changes
        }
        historyList.addLast(blockHistory);
        blockHistoryMap.put(uuid, historyList);
    }

    public static int rollbackAllBlocks(UUID uuid) {
        return rollbackBlocks(uuid, getBlockHistory(uuid).map(List::size).orElse(0));
    }

    public static int rollbackBlocks(UUID uuid, int times) {
        int timesSuccessful = 0;
        Optional<LinkedList<BlockHistoryCollection>> maybeHistoryList = getBlockHistory(uuid);
        if(!maybeHistoryList.isPresent()) {
            return 0;
        }
        LinkedList<BlockHistoryCollection> historyList = maybeHistoryList.get();
        for(int i = 0; i < times; i++) {
            if(historyList.isEmpty()) {
                break;
            }
            historyList.removeLast().rollbackBlocks();
            timesSuccessful++;
        }
        return timesSuccessful;
    }

}
