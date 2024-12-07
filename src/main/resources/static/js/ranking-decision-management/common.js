
//cancel -> refresh page
$('#button-cancel').click(function (){
    location.reload();
});
//rename ranking decision
$('#rename-failed').hide()
$('#rename-success').hide()
$('#rename-save').click(function (){
    let rankingDecisionName = removeExtraSpaces($('#popup-ranking-decision-name').val());
    //nếu name chưa thay đổi thì ko gửi request
    if(rankingDecisionName != $('#ranking-decision-name').val() && rankingDecisionName.length != 0){ // tên mới khác tên cũ và khác rỗng
        $.ajax({
            url: '/api/ranking-decision-api/rename/'+$('#rankingDecisionId').val(),
            type: 'PUT',
            headers: {
                [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
            },
            data:{
                'name' : rankingDecisionName
            },
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8', // Set contentType phù hợp
            processData: true, // jQuery sẽ tự động serialize dữ liệu
            success: function (response, textStatus, jqXHR){
                $('#notificationSuccessModal .modal-body h5').text("Rename successfully!");
                $('#notificationSuccessModal').modal('show');
                $('#notificationSuccessModal').on('hidden.bs.modal', function () {
                    location.reload();
                });
            },
            error: function (jqXHR, textStatus, errorThrown){
                $('#notificationErrorModal .modal-body h5').text(jqXHR.responseText);
                $('#notificationErrorModal').modal('show');
            }
        });
    }else{
        $('#popup-ranking-decision-name').val($('#ranking-decision-name').val());
        $('#rename-cancel')[0].click();
    }
});

$('#finalize-btn').click(function (){
    $.ajax({
        url: '/api/ranking-decision-api/finalize/'+$('#rankingDecisionId').val(),
        type: 'PUT',
        headers: {
            [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
        },
        success: function (response, textStatus, jqXHR) {
            if (jqXHR.status == 200) {
                $('#notificationSuccessModal .modal-body h5').text(response);
                $('#notificationSuccessModal').modal('show');
                $('#notificationSuccessModal').on('hidden.bs.modal', function () {
                    location.reload();
                });
            }
        },
        error: function (jqXHR, textStatus, errorThrown) {
            $('#notificationErrorModal .modal-body h5').text(jqXHR.responseText);
            $('#notificationErrorModal').modal('show');
        }
    });
});

function removeExtraSpaces(str) {
    return str.trim().replace(/\s+/g, ' ');
}

// Close modal notifications on clicking OK or close button (x)
$('#notificationErrorModal, #notificationSuccessModal').on('click', '.btn-primary, .btn-close', function() {
    $(this).closest('.modal').modal('hide');
});