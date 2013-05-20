package org.rulez.magwas.test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

class MyHttpServletResponse implements HttpServletResponse
{
	ByteArrayOutputStream bos = null;
	
	MyHttpServletResponse() {
		bos = new ByteArrayOutputStream();
	}
	
	public String getOutput() {
		return bos.toString();
	}
	public void flushBuffer() throws IOException {
		
		
	}

	public int getBufferSize() {
		
		return 0;
	}

	public String getCharacterEncoding() {
		
		return null;
	}

	public String getContentType() {
		
		return null;
	}

	public Locale getLocale() {
		
		return null;
	}

	public ServletOutputStream getOutputStream() throws IOException {
		
		return null;
	}

	public PrintWriter getWriter() throws IOException {
		return new PrintWriter(bos);
	}

	public boolean isCommitted() {
		
		return false;
	}

	public void reset() {
		
		
	}

	public void resetBuffer() {
		
		
	}

	public void setBufferSize(int arg0) {
		
		
	}

	public void setCharacterEncoding(String arg0) {
		
		
	}

	public void setContentLength(int arg0) {
		
		
	}

	public void setContentLengthLong(long arg0) {
		
		
	}

	public void setContentType(String arg0) {
		
		
	}

	public void setLocale(Locale arg0) {
		
		
	}

	public void addCookie(Cookie arg0) {
		
		
	}

	public void addDateHeader(String arg0, long arg1) {
		
		
	}

	public void addHeader(String arg0, String arg1) {
		
		
	}

	public void addIntHeader(String arg0, int arg1) {
		
		
	}

	public boolean containsHeader(String arg0) {
		
		return false;
	}

	public String encodeRedirectURL(String arg0) {
		
		return null;
	}

	public String encodeRedirectUrl(String arg0) {
		
		return null;
	}

	public String encodeURL(String arg0) {
		
		return null;
	}

	public String encodeUrl(String arg0) {
		
		return null;
	}

	public String getHeader(String arg0) {
		
		return null;
	}

	public Collection<String> getHeaderNames() {
		
		return null;
	}

	public Collection<String> getHeaders(String arg0) {
		
		return null;
	}

	public int getStatus() {
		
		return 0;
	}

	public void sendError(int arg0) throws IOException {
		
		
	}

	public void sendError(int arg0, String arg1) throws IOException {
		
		
	}

	public void sendRedirect(String arg0) throws IOException {
		
		
	}

	public void setDateHeader(String arg0, long arg1) {
		
		
	}

	public void setHeader(String arg0, String arg1) {
		
		
	}

	public void setIntHeader(String arg0, int arg1) {
		
		
	}

	public void setStatus(int arg0) {
		
		
	}

	public void setStatus(int arg0, String arg1) {
		
		
	}

}