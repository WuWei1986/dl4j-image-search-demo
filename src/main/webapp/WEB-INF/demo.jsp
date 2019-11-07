<%@page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="fn" uri="http://java.sun.com/jsp/jstl/functions"%>
<%@taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<html>
<head>
<script type="text/javascript">
	
</script>
</head>
<body>
	<jsp:include flush="false" page="/header.jsp">
	<c:choose>
		<c:when test="${foo != null}">
			${foo.id}
		</c:when>
		<c:otherwise>

		</c:otherwise>
	</c:choose>

	<c:when test="${not empty list && fn:length(list) > 0 }" >
	<c:foreach items="${list}" var="item" varStatus="varStatus">
		<c:if test="${varStatus.count == 1}" >
			<fmt:formatDate value="${item.createTime}" pattern="yyyy.MM.dd" />
		</c:if>
	</c:foreach>
	</c:when>
</body>
</html>
