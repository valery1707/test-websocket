#!/bin/bash

npm install thor
node_modules/.bin/thor --amount 5000 --concurrent 50 --messages 10 --generator src/test/node/thor-generator.js --masked ws://fasten-websocket.herokuapp.com/pure wss://fasten-websocket.herokuapp.com/pure
