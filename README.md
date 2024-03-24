# PunishmentsCore - Minecraft Plugin

PunishmentsCore is a powerful and intuitive Minecraft plugin designed to streamline the process of managing player punishments within the game. By leveraging LiteBans as a dependency, this plugin offers a user-friendly graphical interface, allowing staff members to execute punishment commands efficiently and effectively. With PunishmentsCore, server administrators can ensure a fair and controlled environment, enhancing the gameplay experience for all users.

## Features

- **GUI for Punishments**: Easily access a graphical interface to punish players. The GUI simplifies the process of selecting the type of punishment, ensuring that staff can make decisions quickly and accurately.
- **Leveling punishments**: You can configure each punishment to have different levels. When one level is applied, the next time you punish another player it will use the next level commands.
- **Pending Orders Overview**: View and manage pending punishments that were initiated by other staff members lacking the necessary permissions. This feature ensures that no action is overlooked and helps in maintaining a consistent punishment policy across your staff team.
- **Personal Sanctions History**: Staff members can view their own history of issued punishments, broken down by type. This personal log helps in tracking one's administrative actions and provides a reference for consistent enforcement of rules.

## Commands

- **`/punish <user>`**: Opens the GUI to punish the specified user. This interface allows for the selection of punishment types and durations, streamlining the process of penalizing players.

- **`/orders`**: Access the GUI that lists all pending punishments. This interface is particularly useful for higher-level staff to review and execute actions that could not be completed by lower-level staff due to permission restrictions.

- **`/sanctions`**: Displays a GUI with a summary of the last punishment of each type that the executing staff member has issued. This feature allows for quick reference and ensures that staff can maintain awareness of their disciplinary actions.

## Dependencies

- **LiteBans**: PunishmentsCore requires LiteBans to function. LiteBans is a comprehensive banning plugin that provides the backend necessary for executing and tracking punishments. Ensure LiteBans is installed and configured on your server before adding PunishmentsCore.

## Installation

1. **Download PunishmentsCore**: Obtain the latest version of PunishmentsCore from the official plugin repository.
2. **Ensure LiteBans is Installed**: Verify that LiteBans is installed and properly set up on your server.
3. **Place the Plugin**: Move the PunishmentsCore plugin file into your server's 'plugins' directory.
4. **Restart the Server**: After placing the plugin file, restart your Minecraft server to activate PunishmentsCore.
5. **Configuration (Optional)**: Access the PunishmentsCore configuration files to customize settings and preferences to suit your server's needs.

## Permissions

PunishmentsCore introduces specific permissions to control who can access the various features and commands. Ensure your server's permission system is updated to include these, tailoring access as needed for your staff team.

- **punishmentscore.punish**: Allows access to the `punish` command.
- **punishmentscore.orders**: Grants access to view and manage pending punishments through the `orders` command.
- **punishmentscore.sanctions**: Permits the user to view their punishment history via the `sanctions` command.
- **punishmentscore.force**: Force to open a punish GUI when another staff is using it with the same player.

---
## Examples
![image](https://github.com/Ivanlpc/PunishmentsCore/assets/100932340/3d97bfc4-3c19-4948-b073-e6997747109f)
