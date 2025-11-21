import { NextRequest, NextResponse } from 'next/server';

export function middleware(request: NextRequest) {
  const url = request.nextUrl;

  // Verifica si la URL tiene el parámetro `error`
  if (url.searchParams.has('error')) {
    // Construye la URL absoluta para redirigir
    const redirectUrl = new URL('/', url.origin);
    return NextResponse.redirect(redirectUrl.toString());
  }

  // Permitir que la solicitud continúe
  return NextResponse.next();
}

export const config = {
  matcher: ['/api/auth/:path*'],
};
