package dev.plex.medina.command.exception;

import static dev.plex.medina.util.MedinaUtils.messageString;

public class PlayerNotFoundException extends RuntimeException
{
    public PlayerNotFoundException()
    {
        super(messageString("playerNotFound"));
    }
}
