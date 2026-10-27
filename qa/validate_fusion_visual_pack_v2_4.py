#!/usr/bin/env python3
"""Validate the optional Fusion 1.21.1 habitat resource pack and its art."""
import hashlib
import json
import struct
import sys
import zipfile
from pathlib import Path

ROOT=Path(__file__).resolve().parents[1]
PACK=Path(sys.argv[1]) if len(sys.argv)>1 else ROOT/'build/fusion-visual-pack'
ARCHIVE=Path(sys.argv[2]) if len(sys.argv)>2 else ROOT/'build/distributions/gugugaga-fusion-visuals-v2.4.zip'
ART=ROOT/'src/main/resources/assets/gugugaga/models/block'
KINDS=['bumblebee','mason','leafcutter','squash']
def read(path):
    return json.loads(path.read_text(encoding='utf-8'))
def main():
    pack=read(PACK/'pack.mcmeta')['pack']
    assert pack['pack_format']==34, 'Minecraft 1.21.1 resource pack format must be 34'
    hashes=set()
    for index,kind in enumerate(KINDS):
        for subindex,surface in enumerate(['side','roof']):
            name=f'{kind}_{surface}'
            texture=PACK/f'assets/gugugaga/textures/block/fusion/{name}.png'
            data=texture.read_bytes()
            assert data[:8]==b'\x89PNG\r\n\x1a\n' and struct.unpack('>II',data[16:24])==(32,32),name
            hashes.add(hashlib.sha256(data).hexdigest())
            metadata=read(texture.with_suffix('.png.mcmeta'))['fusion']
            assert metadata==dict(type='random',rows=2,columns=2,randomness_source='position_facing',seed=8300+index*10+subindex),name
        habitat='bumblebee_habitat' if kind=='bumblebee' else kind+'_bee_habitat'
        for suffix in ['','_active']:
            model_id=habitat+suffix
            original=read(ART/f'{model_id}.json')
            enhanced=read(PACK/f'assets/gugugaga/models/block/{model_id}.json')
            assert enhanced.get('loader')=='fusion:model' and enhanced.get('type')=='fusion:base',model_id
            assert enhanced.get('elements')==original.get('elements'),model_id
            assert enhanced['textures']['up']==f'gugugaga:block/fusion/{kind}_roof',model_id
            assert enhanced['textures']['east']==f'gugugaga:block/fusion/{kind}_side',model_id
    assert len(hashes)==8,'Fusion atlases must not be duplicates'
    assert ARCHIVE.is_file(),'missing distributable optional pack'
    with zipfile.ZipFile(ARCHIVE) as z:
        files=set(z.namelist())
        assert 'pack.mcmeta' in files
        assert 'pack.png' in files, 'missing companion pack icon'
        assert z.read('pack.png')[:8]==b'\x89PNG\r\n\x1a\n', 'pack icon is not a PNG'
        for kind in KINDS:
            habitat = 'bumblebee_habitat' if kind == 'bumblebee' else kind + '_bee_habitat'
            assert f'assets/gugugaga/models/block/{habitat}.json' in files
            assert f'assets/gugugaga/models/block/{habitat}_active.json' in files
            for surface in ['side','roof']:
                assert f'assets/gugugaga/textures/block/fusion/{kind}_{surface}.png.mcmeta' in files
    print('FUSION_VISUAL_PACK_PASS: 8 Fusion models, 8 distinct 32x32 random atlases, correct optional ZIP and fallback-preserving block geometry.')
if __name__=='__main__':
    main()
