from lxml import etree, html
import json
import requests
import pyperclip



report = {
	"count": 0,
}

url_base = "https://www.surbusalmeria.es"

response_bus_lines = requests.get(f"{url_base}/lineas")

bus_lines_html = response_bus_lines.text
root = html.fromstring(bus_lines_html)

bus_data = []

for line_node in root.xpath("//div[@class='links']/a[@class='lines']"): 
	
	link = line_node.attrib.get('href')
	color = line_node.xpath("string(span[@class='text']/span[@class='title']/@style)").replace('color:', '').replace(';', '')
	line_description = line_node.xpath("string(span[@class='text']/span[@class='title']/text())")

	line_number = link.replace('/linea/', '')

	print(f'line number: {line_number}')

	bus_stops = []

	# scrap bus stops
	response_bus_stops = requests.get(f'{url_base}/tiempos-de-espera/linea/{line_number}')
	bus_stops_html = response_bus_stops.text
	root_bus_stops = html.fromstring(bus_stops_html)

	
	for bus_stop_node in root_bus_stops.xpath("//li[@class='waitTimeBusStop']"): 

		code = bus_stop_node.xpath("string(a/span/span[@class='label']/text())")
		name = bus_stop_node.xpath("string(div/span[@class='name']/text())")
		bus_stops.append(
			{
				'code': code,
				'name': name,
				'lineId': line_number,
				'waitTimeCode': '',
				'coordinates': [0, 0],
			}
		)

	
	bus_line = {
		'id': line_number,
		'color': color,
		'name': f'Línea {line_number}',
		'description': line_description,
		'busStops': bus_stops,
		'waitTimeCode': '',
		'path': [[0, 0], [1, 1]],
	}

	bus_data.append(bus_line)
	report['count'] += 1

	# REMOVE THIS FOR SCRAP ALL LINES
	if report['count'] == 3:
		break

print(f'\n\nReport: {json.dumps(report, indent=4)}')

data_formatted = json.dumps(bus_data, indent=4)

print(f'\n\nBus data: {data_formatted}')

with open('bus_data.json', 'w') as f:
	f.write(data_formatted)


pyperclip.copy(data_formatted)