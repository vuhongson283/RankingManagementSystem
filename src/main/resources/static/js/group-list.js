$(document).ready(function() {
    var table = $('#groupTable').DataTable({
        "pageLength": 4,
        "lengthChange": false
    });

    function bindDeleteButton() {
        $('.table #deleteButton').on('click', function(event){
            event.preventDefault();
            var href = $(this).attr('href');
            $('#deleteModal #delRef').attr('href', href);
            $('#deleteModal').modal();
        });

        $('#deleteModal #delRef').on('click', function(event) {
            event.preventDefault();
            var href = $(this).attr('href');
            $.ajax({
                url: href,
                type: 'DELETE',
                headers: {
                    [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
                },
                success: function(result) {
                    // Set flag in local storage
                    localStorage.setItem('showToastr', 'true');

                    $('#deleteModal').modal('hide');
                    location.reload(); // Reload the page
                },
                error: function(err) {
                    console.log(err);
                }
            });
        });
    }

    function bindSaveButton() {
        $('#addGroupForm').submit(function(event) {
            event.preventDefault();
            var groupName = $('#group-name').val();
            var valid = true;
            var regex = /^[\w\sÀÁÂÃÈÉÊÌÍÒÓÔÕÙÚĂĐĨŨƠàáâãèéêìíòóôõùúăđĩũơƯĂẠẢẤẦẨẪẬẮẰẲẴẶẸẺẼỀỀỂưăạảấầẩẫậắằẳẵặẹẻẽềềểẾỄỄỆỈỊỌỎỐỒỔỖỘỚỜỞỠỢỤỦỨỪễệỉịọỏốồổỗộớờởỡợụủứừửỮỰỲỴÝỶỸửữựỳỵỷỹ]+$/;

            if (!groupName) {
                $('#invalidCharsModal .modal-body h5').text('Group Name is required.');
                $('#invalidCharsModal').modal('show');
                valid = false;
            } else if (!regex.test(groupName)) {
                $('#invalidCharsModal .modal-body h5').text('Invalid characters are not allowed.');
                $('#invalidCharsModal').modal('show');
                valid = false;
            } else {
                $('#group-name').removeClass('is-invalid');
            }

            if (valid) {
                $.ajax({
                    url: $(this).attr('action'),
                    method: $(this).attr('method'),
                    headers: {
                        [$("meta[name='_csrf_header']").attr("content")]: $("meta[name='_csrf']").attr("content")
                    },
                    contentType: 'application/x-www-form-urlencoded; charset=UTF-8', // Ensure sending JSON
                    data: { groupName: groupName }, // Send data as JSON
                    success: function(response) {
                        $('#addModal').modal('hide');
                        $('#successModal').modal('show');
                        $('#successModal').on('hidden.bs.modal', function () {
                            location.reload();
                        });
                    },
                    error: function(response) {
                        $('#addModal').modal('hide');
                        if (response.status === 409) { // Conflict
                            $('#existsModal').modal('show');
                        } else {
                            $('#errorModal').modal('show');
                        }
                    }
                });
            }
        });
    }

    bindDeleteButton();
    bindSaveButton();

    table.on('draw', function() {
        bindDeleteButton();
        bindSaveButton();
    });

    // Check for flag in local storage
    if (localStorage.getItem('showToastr') === 'true') {
        toastr.success('Delete successful!', 'Success', {
            positionClass: 'toast-top-right',
            timeOut: 3000,
            closeButton: true,
            progressBar: true
        });

        // Remove flag from local storage
        localStorage.removeItem('showToastr');
    }

    // Close modal notifications on clicking OK or close button (x)
    $('#successModal, #errorModal, #existsModal, #invalidCharsModal').on('click', '.btn-primary, .btn-close', function() {
        $(this).closest('.modal').modal('hide');
    });
});
