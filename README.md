## Overview
Price Checker is a plain Java application that allows users to:
- Subscribe to product price updates
(Currently supports goldapple.ru)
- Receive notifications via a Telegram bot

## Features
Users can:
- Register via the /register command
- Add products to track: 
- - Use the /subscribe command
- - Send the product link in the next message
- List all existing subscriptions with /subscriptions command
- Remove subscription with buttons on /subscriptions command
- Receive price change notifications directly in Telegram when the monitored product's price changes

## Tech Stack
Java 21, PostgreSQL 16, HikariCP, Apache Kafka, TelegramBots API, Jsoup