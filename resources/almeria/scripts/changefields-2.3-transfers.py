import json
import sys

with open('bus_data.json', 'r') as file_bus_data_in:
	json_bus_data = json.loads(file_bus_data_in.read())

with open('transfer_info.json', 'r') as file_transfer_info_in:
	json_transfer_info = json.loads(file_transfer_info_in.read())

for index, bus_line in enumerate(json_bus_data):

	bus_line_2 = json_transfer_info[index]

	if not bus_line['id'] == bus_line_2['id']:
		print(f'Lines dont match: {bus_line["id"]}')
		sys.exit()

	for index_bs, bus_stop in enumerate(bus_line['busStops']):

		bus_stop_2 = bus_line_2['busStops'][index_bs]

		if not bus_stop['name'] == bus_stop_2['name']:
			print(f'Bus stop dont match: {bus_stop["name"]}')
			sys.exit()

		bus_stop['transfer'] = bus_stop_2['transfer']

data_formatted = json.dumps(json_bus_data, indent=4)
print(data_formatted)

with open('bus_data.json', 'w') as file_bus_data_out:
    file_bus_data_out.write(data_formatted)

