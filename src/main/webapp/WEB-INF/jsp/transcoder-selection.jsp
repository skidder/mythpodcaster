<%@ page session="false"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@include file="/WEB-INF/jsp/include.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
<title>MythPodcaster :: Transcoder Selection</title>
<link rel="stylesheet" href="styles/html.css" type="text/css">
</head>
<body>
<h1>Select a Transcode Profile</h1>

  <form method="POST">
      <c:forEach items="${transcoders}" var="transcoder">
      	<input type="radio" name="transcodeProfile" value="<c:out value="${transcoder}" />"><c:out value="${transcoder}" /><br/>
      </c:forEach>
    <input type="hidden" name="seriesId" value="<c:out value="${seriesId}" />" />
    <input type="hidden" name="action" value="<c:out value="${action}" />" />
    <input type="submit">
  </form>

</body>
</html>
