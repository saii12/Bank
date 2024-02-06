<%@page import="java.math.BigInteger"%>
<%@page import="java.security.SecureRandom"%>
<%@page import="java.net.URLEncoder"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!-- header -->
<%@ include file="/WEB-INF/view/layout/header.jsp"%>

<div class="col-sm-8">
	<h2>로그인</h2>
	<h5>
		어서오세요 <span></span> 환영합니다
	</h5>

	<form action="/user/sign-in" method="post">
		<div class="form-group">
			<label for="username">username:</label>
			<!-- for에는 커서가 나타날 id를 적어야함 -->
			<input type="text" name="username" class="form-control"
				placeholder="Enter username" id="username" value="길동">
		</div>
		<div class="form-group">
			<label for="pwd">Password:</label> <input type="password"
				name="password" class="form-control" placeholder="Enter password"
				id="pwd" value="1234">
		</div>

		<button type="submit" class="btn btn-primary">로그인</button>
		<a href="https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=ce1e33f8406e67989901a6130e4ff70b&redirect_uri=http://localhost:80/user/kakao-callback">
			<img alt="" src="/images/kakao_login_small.png" width="75" height="38">
		</a>
		 <%
		    String clientId = "KDwJYTBj0wO0YT2VBsoQ";//애플리케이션 클라이언트 아이디값";
		    String redirectURI = URLEncoder.encode("http://localhost:80/user/naver-callback", "UTF-8");
		    SecureRandom random = new SecureRandom();
		    String state = new BigInteger(130, random).toString();
		    String apiURL = "https://nid.naver.com/oauth2.0/authorize?response_type=code";
		    apiURL += "&client_id=" + clientId;
		    apiURL += "&redirect_uri=" + redirectURI;
		    apiURL += "&state=" + state;
		    session.setAttribute("state", state);
		 %>
  <a href="<%=apiURL%>"><img height="50" src="http://static.nid.naver.com/oauth/small_g_in.PNG"/></a>
	</form>
</div>
</br>
</div>



<!-- footer -->
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>