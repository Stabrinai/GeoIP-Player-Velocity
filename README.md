# GeoIP-Player Plugin for Velocity

GeoIP-Player is a velocity server plugin that allows querying player geographic locations based on the GeoLite2 database.

## Features
- Retrieve the geographical location of players using the GeoLite2 database.
- Provides various commands for querying and managing the plugin.

## Commands

| Command | Description | Permission Node |
|---------|-------------|----------------|
| `/geoip list` | List all online players' geographic locations | `geoip.list` |
| `/geoip lookup <player>` | Lookup a specific player's geographic location | `geoip.lookup` |
| `/geoip reload` | Reload the configuration file | `geoip.reload` |

## Installation
1. Download the plugin jar file and place it in the `plugins` folder of your velocity server.
2. Restart or reload the server.
3. Ensure the GeoLite2 database is properly set up and accessible by the plugin.

## Configuration
After installation, you can modify the configuration file located in the `plugins/geoip-player/` directory. Reload the plugin using `/geoip reload` after making changes.

## Dependencies
- [GeoLite2 Database](https://dev.maxmind.com/geoip/geoip2/geolite2/) (Required)

## License
This plugin utilizes GeoLite2 data created by MaxMind, available from [MaxMind](https://www.maxmind.com). Ensure compliance with their license when using this data.

## Credits
Developed by Stabrinai.

For support or feature requests, please open an issue on the repository.

