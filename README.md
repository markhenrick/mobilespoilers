# mobilespoilers

[![Build Status](https://travis-ci.com/markhenrick/mobilespoilers.svg?branch=master)](https://travis-ci.com/markhenrick/mobilespoilers) [![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

This bot is a temporary solution for a minor UI problem on [Discord](https://discordapp.com/) - there is no way to mark an image as a spoiler on mobile

This bot adds a `!spoil` command to do that

I've deliberately over-engineered this a bit to make it more fun. The main complication is that it uses an SQLite DB to track who owns what spoilers, so the user can delete it later

It is currently very much a WIP
