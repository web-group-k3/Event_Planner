
const getFingerprint = async (): Promise<string> => {
    const components = [
        navigator.userAgent,
        navigator.language,
        `${screen.width}x${screen.height}`,
        new Date().getTimezoneOffset().toString(),
        navigator.hardwareConcurrency?.toString() ?? '0',
        navigator.platform,
    ].join('|');

    const buffer = await crypto.subtle.digest(
        'SHA-256',
        new TextEncoder().encode(components)
    );

    const hash = Array.from(new Uint8Array(buffer))
        .map(b => b.toString(16).padStart(2, '0'))
        .join('');

    return 'fp-' + hash.substring(0, 32);
};

const getLocalStorageId = (): string => {
    let id = localStorage.getItem('anonymous_id');
    if (!id) {
        id = 'ls-' + crypto.randomUUID();
        localStorage.setItem('anonymous_id', id);
    }
    return id;
};

export const getAnonymousHeaders = async (): Promise<Record<string, string>> => {
    const localId = getLocalStorageId();
    const fingerprint = await getFingerprint();
    return {
        'X-Anonymous-Id': localId,
        'X-Fingerprint': fingerprint,
    };
};