package de.craftinc.inventories.utils;

public class ConfigurationKeys
{
    public static final String doGameModeSwitchKey = "perform-game-mode-switches";
    public static final String doStatisticsKey = "store-player-stats";
    public static final String logSaveTimerMessagesKey = "log-saving-to-disk";
    public static final String saveTimerIntervalKey = "save-interval";
    public static final String loadInventoriesOnLoginKey = "load-inventory-on-login";
    public static final String doVanillaImportKey = "perform-vanilla-import";
    public static final String vanillaImportGroup = "vanilla-import-group";
    public static final String languageKey = "language";

    public static final String gameModesGroupKey = "game-modes."; // keep the '.' at the end!
    public static final String defaultGameModeKey = "game-modes.default";

    public static final String exemptPlayersGroupKey = "exempt";

    public static final String worldGroupsKey = "groups";

    // hidden messages
    public static final String hideMessagesGroupKey = "message-hidden."; // keep the '.' at the end!
    public static final String hideDiedMessageKey = "died-message";
    public static final String hideChangedInventoryKey = "changed-message";
    public static final String hideNotChangedInventoryKey = "no-change-message";
    public static final String hideLoadedInventoryKey = "loaded-message";
}
