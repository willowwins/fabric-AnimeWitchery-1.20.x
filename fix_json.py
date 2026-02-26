import json

# Simplified, robust surface rule
# Grass on top, dirt below, stone for everything else
surface_rule = {
    "type": "minecraft:sequence",
    "sequence": [
        {
            "type": "minecraft:condition",
            "if_true": {
                "type": "minecraft:vertical_gradient",
                "false_at_and_above": { "above_bottom": 5 },
                "random_name": "minecraft:bedrock_floor",
                "true_at_and_below": { "above_bottom": 0 }
            },
            "then_run": { "type": "minecraft:block", "result_state": { "Name": "minecraft:bedrock" } }
        },
        {
            "type": "minecraft:condition",
            "if_true": {
                "type": "minecraft:stone_depth",
                "offset": 0,
                "surface_type": "floor",
                "add_surface_depth": False,
                "secondary_depth_range": 0
            },
            "then_run": {
                "type": "minecraft:condition",
                "if_true": { "type": "minecraft:water", "offset": 0, "surface_depth_multiplier": 0, "add_stone_depth": False },
                "then_run": { "type": "minecraft:block", "result_state": { "Name": "minecraft:grass_block" } }
            }
        },
        {
            "type": "minecraft:condition",
            "if_true": {
                "type": "minecraft:stone_depth",
                "offset": 1,
                "surface_type": "floor",
                "add_surface_depth": False,
                "secondary_depth_range": 0
            },
            "then_run": { "type": "minecraft:block", "result_state": { "Name": "minecraft:dirt" } }
        },
        { "type": "minecraft:block", "result_state": { "Name": "minecraft:stone" } }
    ]
}

def fix():
    with open('router.json', 'r', encoding='utf-8') as f:
        router = json.load(f)
    
    with open('src/main/resources/data/animewitchery/worldgen/noise_settings/paradise_lost.json', 'r', encoding='utf-8') as f:
        data = json.load(f)
    
    data['noise_router'] = router
    data['surface_rule'] = surface_rule
    data['noise']['height'] = 640
    data['noise']['min_y'] = -64
    
    with open('src/main/resources/data/animewitchery/worldgen/noise_settings/paradise_lost.json', 'w', encoding='utf-8') as f:
        json.dump(data, f, indent=2)
    print("Fix completed!")

if __name__ == '__main__':
    fix()
