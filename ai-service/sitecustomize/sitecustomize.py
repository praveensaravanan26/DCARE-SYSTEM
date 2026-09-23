import platform
_orig_mac_ver = platform.mac_ver
def _fixed_mac_ver(*args, **kwargs):
    v = _orig_mac_ver(*args, **kwargs)
    if not v[0]:
        return ('14.5.0', ('', '', ''), 'arm64')
    return v
platform.mac_ver = _fixed_mac_ver
