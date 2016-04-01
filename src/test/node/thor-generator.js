'use strict';

/**
 * Generate a UTF-8 messages that we will be send to a connected client.
 *
 * @async
 * @param {Number} size The specified in bytes for the message.
 * @param {Function} fn The callback function for the data.
 * @public
 */
exports.utf8 = function utf(size, fn) {
	const message = {
		"type": "CURRENT_TIME",
		"sequence_id": "bfd907b6-df25-4f73-80b9-b2c5d3ed878f",//todo Random
		"data": {
			"api_token": "ab7d2fbb-18f4-4149-819e-3404f35e1a75"//todo Env
		}
	};
	fn(undefined, JSON.stringify(message));
};
