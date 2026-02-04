
export function deepFind(list: any[], value: any) {
    return list?.find(e => e.value === value) || {};
}
