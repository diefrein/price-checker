## Overview
Price Checker is a plain Java application that allows users to:
- Subscribe to product price updates
(Currently supports goldapple.ru)
- Receive notifications via a Telegram bot

## Features
Users can:
- Register via the /register command
- Add products to track: 
- - Use the /product command
- - Send the product link in the next message
- Receive price change notifications directly in Telegram when the monitored product's price changes

## Tech Stack
Java 21, PostgreSQL 16, HikariCP, Apache Kafka, TelegramBots API, Jsoup