export interface JwtPayload {
  sub: string;
  email?: string;
  preferred_username?: string;
  iat?: number;
  exp?: number;
  iss?: string;
  aud?: string | string[];
  realm_access?: {
    roles?: string[];
  };
  resource_access?: {
    [client: string]: {
      roles?: string[];
    };
  };
  roles?: string[];
  [key: string]: unknown;
}

export function decodeJwt(token: string): JwtPayload | null {
  try {
    const parts = token.split('.');
    if (parts.length !== 3) return null;

    const base64 = parts[1].replace(/-/g, '+').replace(/_/g, '/');
    const jsonPayload = decodeURIComponent(
      atob(base64)
        .split('')
        .map(c => '%' + ('00' + c.charCodeAt(0).toString(16)).slice(-2))
        .join(''),
    );

    return JSON.parse(jsonPayload) as JwtPayload;
  } catch {
    return null;
  }
}

export function getSubjectFromToken(token: string): string | null {
  return decodeJwt(token)?.sub ?? null;
}

export function getEmailFromToken(token: string): string | null {
  return decodeJwt(token)?.email ?? null;
}

export function isTokenExpired(token: string): boolean | null {
  const payload = decodeJwt(token);
  if (!payload?.exp) return null;
  return Date.now() >= payload.exp * 1000;
}

export function getTokenExpiration(token: string): number | null {
  const payload = decodeJwt(token);
  if (!payload?.exp) return null;
  return payload.exp * 1000;
}

export function getRolesFromToken(token: string): string[] {
  const payload = decodeJwt(token);
  if (!payload) return [];

  const roles: string[] = [];

  if (payload.realm_access?.roles) {
    roles.push(...payload.realm_access.roles);
  }

  if (payload.resource_access) {
    for (const client of Object.values(payload.resource_access)) {
      if (client.roles) roles.push(...client.roles);
    }
  }

  if (payload.roles && Array.isArray(payload.roles)) {
    roles.push(...payload.roles);
  }

  return [...new Set(roles.map(r => r.toLowerCase()))];
}
