from lxml import etree, html
import json
import requests
import pyperclip
import urllib.parse
import re

def decode_polyline(polyline_str):
    index, lat, lng = 0, 0, 0
    coordinates = []
    changes = {'latitude': 0, 'longitude': 0}

    # Coordinates have variable length when encoded, so just keep
    # track of whether we've hit the end of the string. In each
    # while loop iteration, a single coordinate is decoded.
    while index < len(polyline_str):
        # Gather lat/lon changes, store them in a dictionary to apply them later
        for unit in ['latitude', 'longitude']: 
            shift, result = 0, 0

            while True:
                byte = ord(polyline_str[index]) - 63
                index+=1
                result |= (byte & 0x1f) << shift
                shift += 5
                if not byte >= 0x20:
                    break

            if (result & 1):
                changes[unit] = ~(result >> 1)
            else:
                changes[unit] = (result >> 1)

        lat += changes['latitude']
        lng += changes['longitude']

        coordinates.append((lat / 100000.0, lng / 100000.0))

    return coordinates


def fix_encoding(text):
	try:
		return text.encode('iso-8859-1').decode('utf-8')
	except Exception as e:
		#print(e)
		#print("error with text: " + text)
		return text


report = {
	"count": 0,
}

url_base = "https://www.tua.es"

response_bus_lines = requests.get(f"{url_base}/es/lineas-y-horarios")
print(response_bus_lines.encoding)
print(response_bus_lines.apparent_encoding)

bus_lines_html = response_bus_lines.text
root = html.fromstring(bus_lines_html)

bus_data = []

line_number = 0

# Get line paths
lines_coords = []
lines_coords_encoded = re.findall("coordenadasLineas.push.*;", bus_lines_html)
for line_coords_encoded in lines_coords_encoded:
	line_coords_encoded = line_coords_encoded.replace("coordenadasLineas.push('","").replace("');", "")
	line_coords_encoded = line_coords_encoded.replace("\\\\", "\\").strip()
	coords = decode_polyline(line_coords_encoded)
	lines_coords.append(coords)

print('Number of lines: ' + str(len(lines_coords)))


for line_node in root.xpath("//div[@id='lineas']//span"): 

	print("\n")

	line_number += 1
	color = line_node.attrib.get('style')
	color = color.replace("background:", "")
	link = line_node.xpath("string(a/@href)")
	line_name = fix_encoding(line_node.xpath("string(a/text())")) 

	#if line_number != 11: continue

	print(f'line name: {line_name}')

	bus_stops = []

	# scrap bus stops and line polyline
	response_bus_stops = requests.get(f'{url_base}{link}')
	bus_stops_html = response_bus_stops.text
	root_bus_stops = html.fromstring(bus_stops_html)

	line_description_parts = root_bus_stops.xpath("//a[@href='#ida']/strong/text()") # Array of 2 (origin, destination)
	line_description = line_description_parts[0] + ' - ' + line_description_parts[1]
	line_description = fix_encoding(line_description)
	print(line_description)

	# Bus stops data
	latitudes = re.findall("marcadorLatitud = '4.*';", bus_stops_html)
	longitudes = re.findall("marcadorLongitud = '-.*';", bus_stops_html)
	info_bus_stops = re.findall("marcadorInfo = {codigo:.*};", bus_stops_html) # These arrays must have exactly the same size

	for i, item in enumerate(latitudes):

		latitude = latitudes[i].replace("marcadorLatitud = '", "").replace("';", "")
		longitude = longitudes[i].replace("marcadorLongitud = '", "").replace("';", "")

		bus_stop_code = re.search(r"codigo: '(.*?)', titulo", info_bus_stops[i]).group(1) # https://stackoverflow.com/a/32680048/1365440
		bus_stop_name = re.search(r"titulo: '(.*?)', url", info_bus_stops[i]).group(1)
		bus_stop_name = fix_encoding(bus_stop_name)

		bus_stop_coords = [float(latitude), float(longitude)]

		bus_stops.append(
			{
				'code': bus_stop_code,
				'name': bus_stop_name,
				'lineId': line_number,
				'lineName': line_name,
				'coordinates': bus_stop_coords,
			}
		)
	
	
	bus_line = {
		'id': line_number,
		'lineName': line_name,
		'colorHex': color,
		'name': f'Línea {line_name}',
		'description': line_description,
		'busStops': bus_stops,
		'path': lines_coords[line_number-1],
	}

	bus_data.append(bus_line)
	report['count'] += 1



print(f'\n\nReport: {json.dumps(report, indent=4)}')

data_formatted = json.dumps(bus_data)

#print(f'\n\nBus data: {data_formatted}')

with open('bus_data.json', 'w') as f:
	f.write(data_formatted)


pyperclip.copy(data_formatted)
