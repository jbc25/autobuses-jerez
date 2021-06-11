import json


# Second add paths to the main json
with open('bus_data_old.json', 'r') as file_bus_data_in:
	json_bus_data = json.loads(file_bus_data_in.read())

json_bus_data_ordered = []

for bus_line in json_bus_data:

	for bus_stop in bus_line['busStops']:
		bus_stop['waitTimeCode'] = bus_stop['extras']['waitTimeCode']
		bus_stop['waitTimeCodeLine'] = bus_line['extras']['waitTimeCode']
		del bus_stop['extras']

	bus_line_ordered_fields = {
		'id': bus_line['id'],
		'name': bus_line['name'],
		'description': bus_line['description'],
		'color': 0,
		'colorHex': bus_line['colorHex'],
		'waitTimeCode': bus_line['extras']['waitTimeCode'],
		'busStops': bus_line['busStops'],
		'path': bus_line['path']
		}

	json_bus_data_ordered.append(bus_line_ordered_fields)


data_formatted = json.dumps(json_bus_data_ordered, indent=4)
print(data_formatted)

with open('bus_data.json', 'w') as file_bus_data_out:
    file_bus_data_out.write(data_formatted)

