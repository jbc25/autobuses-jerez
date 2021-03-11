from lxml import etree
import json


tree = etree.parse('Lineas_almeria.kml')
root = tree.getroot()

# Remove namespace prefixes
for elem in root.getiterator():
    elem.tag = etree.QName(elem).localname
# Remove unused namespace declarations
etree.cleanup_namespaces(root)

report = {
	"count": 0,
	"count_without_direccion": 0,
	"count_without_cod_postal": 0,
	"without_direccion": [],
	"without_cod_postal": [],
}

# First create a dictionary of bus line path coordinates
lines_paths = {}

for placemark in root.xpath('//Document/Placemark'):

	id_line = placemark.xpath('string(name/text())').replace('Línea ', '')
	coords_text = placemark.xpath('string(LineString/coordinates/text())')
	coords_lines = coords_text.split('\n')

	path_coords = []

	for coords_line in coords_lines:

		if ',' not in coords_line:
			continue

		coords_parts = coords_line.strip().split(',')
		path_coords.append([coords_parts[1], coords_parts[0]])

	lines_paths[id_line] = path_coords

print(lines_paths)


# Second add paths to the main json
with open('bus_data.json', 'r') as file_bus_data_in:
	json_bus_data = json.loads(file_bus_data_in.read())
	for bus_line in json_bus_data:
		bus_line['path'] = lines_paths[str(bus_line['id'])]

	data_formatted = json.dumps(json_bus_data, indent=4)
	print(data_formatted)

with open('bus_data.json', 'w') as file_bus_data_out:
    file_bus_data_out.write(data_formatted)
