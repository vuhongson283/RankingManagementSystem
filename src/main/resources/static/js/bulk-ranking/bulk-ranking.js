{
    var employeeIds = [];

    $('#export-btn').click(function () {
        employeeIds = $('input.employee-checkbox:checked').map(function () {
            return this.value;
        }).get();
        $.ajax({
            url: '/api/excel-template/' + $('#groupId').val(),
            type: 'GET',
            data: {'employeeIds': employeeIds},
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            processData: true, // jQuery sẽ tự động serialize dữ liệu
            headers: {
                [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
            },
            xhrFields: {
                responseType: 'blob' // Định dạng phản hồi là Blob
            },
            success: function (response, textStatus, jqXHR) {
                var url = window.URL.createObjectURL(response);
                var a = document.createElement('a');
                a.href = url;
                a.download = 'Bulk Assess(' + new Date($.now()) + ').xlsx';
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $('#notificationErrorModal .modal-body h5').text("Error occurred exporting file. Please try again.")
                $('#notificationErrorModal').modal('show');

            }
        });
    });

    $('.download-imported-file').click(function () {
        let rankingHistoryId = $(this).attr('data-historyid');
        let fileName = $(this).text();
        $.ajax({
            url: '/api/excel-template/download-imported-file/' + rankingHistoryId,
            type: 'GET',
            contentType: 'application/x-www-form-urlencoded; charset=UTF-8',
            processData: true, // jQuery sẽ tự động serialize dữ liệu
            headers: {
                [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
            },
            xhrFields: {
                responseType: 'blob' // Định dạng phản hồi là Blob
            },
            success: function (response, textStatus, jqXHR) {
                var url = window.URL.createObjectURL(response);
                var a = document.createElement('a');
                a.href = url;
                a.download = fileName;
                a.click();
                a.remove();
                window.URL.revokeObjectURL(url);
            },
            error: function (jqXHR, textStatus, errorThrown) {
                $('#notificationErrorModal .modal-body h5').text("Error occurred exporting file. Please try again.")
                $('#notificationErrorModal').modal('show');
            }
        });
    });

    $('#upload-bulk-ranking-btn').click(function () {
        const form = $('#bulking-ranking-form-data')[0];
        const formData = new FormData(form);
        if ($('#rank-file')[0].files.length > 0) {
            $.ajax({
                url: '/api/excel-template/' + $('#groupId').val(),
                type: 'POST',
                data: formData,
                processData: false,
                headers: {
                    [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
                },
                contentType: false,
                success: function (response) {
                    $('#notificationSuccessModal .modal-body h5').text(response);
                    $('#notificationSuccessModal').modal('show');
                    $('#notificationSuccessModal').on('hidden.bs.modal', function () {
                        location.reload();
                    });
                },
                error: function (jqXHR, textStatus, errorThrown) {
                    $('#notificationErrorModal .modal-body h5').text(jqXHR.responseText);
                    $('#notificationErrorModal').modal('show');
                }
            })
        }
        ;
    });

    // Close modal notifications on clicking OK or close button (x)
    $('#notificationErrorModal, #notificationSuccessModal').on('click', '.btn-primary, .btn-close', function () {
        $(this).closest('.modal').modal('hide');
    });
}