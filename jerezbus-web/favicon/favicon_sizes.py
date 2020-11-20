from PIL import Image
import os

if not os.path.exists('generated'):
    os.makedirs('generated')

image = Image.open('ic_app_800_transparent.png')

new_image = image.resize((584, 584))
new_image.save('generated/icon.png')

new_image = image.resize((192, 192))
new_image.save('generated/apple-icon.png')
new_image.save('generated/apple-icon-precomposed.png')

platforms_sizes = [
	{
		'name': 'android-icon',
		'sizes': [36, 48, 72, 96, 144, 192],
	},
	{
		'name': 'apple-icon',
		'sizes': [57, 60, 72, 76, 114, 120, 144, 152, 180],
	},
	{
		'name': 'favicon',
		'sizes': [16, 32, 96],
	},
	{
		'name': 'ms-icon',
		'sizes': [70, 144, 150, 310],
	},
]

for platforms_size in platforms_sizes:
	for size in platforms_size['sizes']:	
		new_image = image.resize((size, size))
		new_image.save(f'generated/{platforms_size["name"]}-{size}x{size}.png')