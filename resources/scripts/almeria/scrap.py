from lxml import etree, html
import json
import requests
import pyperclip
import urllib.parse
import re



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

	# Extract wat time codes
	script_code = root_bus_stops.xpath("string(//script[@type='text/javascript']/text())")

	configure_button_lines = re.findall('ConfigureButton(.*);', script_code)

	bus_line_waitTimeCode = ''
	bus_stops_waitTimeCode = {}

	for configure_button_line in configure_button_lines:
		configure_button_line = re.sub('[\(\)\"\s]', '', configure_button_line)
		line_parts = configure_button_line.split(',')
		bus_line_waitTimeCode = line_parts[1]
		bus_stops_waitTimeCode[line_parts[0]] = line_parts[2]

	
	for bus_stop_node in root_bus_stops.xpath("//li[@class='waitTimeBusStop']"): 

		button_name = bus_stop_node.attrib.get('id')

		bus_stop_waitTimeCode = bus_stops_waitTimeCode[button_name]

		bus_stop_code = bus_stop_node.xpath("string(a/span/span[@class='label']/text())")
		bus_stop_name = bus_stop_node.xpath("string(div/span[@class='name']/text())")

		print(f'Retrieving bus stop info. Code: {bus_stop_code}')

		response_bus_stop = requests.get(f'{url_base}/tiempos-de-espera/parada/{bus_stop_code}')
		root_bus_stop = html.fromstring(response_bus_stop.text)
		link_google_maps = root_bus_stop.xpath("string(//a[@class='openMap']/@href)")
		query_string = urllib.parse.urlparse(link_google_maps).query
		params = dict(urllib.parse.parse_qsl(query_string))
		coords_inverted = params['destination'].split(",")
		bus_stop_coords = [float(coords_inverted[1]), float(coords_inverted[0])]



		bus_stops.append(
			{
				'code': bus_stop_code,
				'name': bus_stop_name,
				'lineId': line_number,
				'waitTimeCode': bus_stop_waitTimeCode,
				'coordinates': bus_stop_coords,
			}
		)

		
	
	bus_line = {
		'id': line_number,
		'color': color,
		'name': f'Línea {line_number}',
		'description': line_description,
		'busStops': bus_stops,
		'waitTimeCode': bus_line_waitTimeCode,
		'path': [[36.843724859578046, -2.450765243421731]],
	}

	bus_data.append(bus_line)
	report['count'] += 1

	# REMOVE THIS FOR SCRAP ALL LINES
	#if report['count'] == 1:
	#	break

#print(f'\n\nReport: {json.dumps(report, indent=4)}')

data_formatted = json.dumps(bus_data, indent=4)

print(f'\n\nBus data: {data_formatted}')

with open('bus_data.json', 'w') as f:
	f.write(data_formatted)


pyperclip.copy(data_formatted)
