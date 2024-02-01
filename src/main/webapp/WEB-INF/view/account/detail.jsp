<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<!-- header -->
<%@ include file="/WEB-INF/view/layout/header.jsp"%>


<h2>계좌 상세보기(인증)</h2>
<h5>어서오세요 환영합니다</h5>
</br>
<!-- col-sm-8 에서 md로 바꿈 -->
<div class="col-md-12 bg-light" id="detail--table">

	<div class="container" style="padding-top: 50px">
		<table class="table table-bordered">
			<thead>
				<tr>
					<th>${principal.username}님의 계좌</th>
					<th>계좌번호 : ${account.number}</th>
					<th>잔액 : ${account.formatBalance()}</th>
				</tr>
			</thead>
			<tbody>
				<tr>
					<td><a href="/account/detail/${account.id}">전체 조회</a></td>
					<td><a href="/account/detail/${account.id}?type=deposit">입금
							조회</a></td>
					<td><a href="/account/detail/${account.id}?type=withdraw">출금
							조회</a></td>
				</tr>
			</tbody>
		</table>
	</div>
	</br> </br>
	<div class="container" style="padding-bottom: 50px">
		<table class="table">
			<thead>
				<tr>
					<th>날짜</th>
					<th>보낸이</th>
					<th>받은이</th>
					<th>입출금금액</th>
					<th>계좌잔액</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach var="history" items="${historyList}">
					<tr>
						<td>${history.formatCreatedAt()}</td>
						<td>${history.sender}</td> 
						<td>${history.receiver}</td>
						<td>${history.amount}</td>
						<td>${history.formatBalance()}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
	</div>


</div>
</br>
</div>



<!-- footer -->
<%@ include file="/WEB-INF/view/layout/footer.jsp"%>