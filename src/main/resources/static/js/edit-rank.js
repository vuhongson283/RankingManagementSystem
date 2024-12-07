$(document).ready(function () {
    // Kiểm tra xem successMessage có tồn tại hay không
    var successMessage = [[${successMessage != null}]];
    if (successMessage) {
        $('#successModal').modal('show');
    }

    // Kiểm tra xem errorMessage có tồn tại hay không
    var errorMessage = [[${errorMessage != null}]];
    if (errorMessage) {
        $('#errorModal').modal('show');
    }

    // Toggle visibility of the choose decision dropdown based on the checkbox state
    $('#cloneDecision').change(function () {
        if ($(this).is(':checked')) {
            $('#chooseDecisionDiv').show();
            $('#rank-name').removeAttr('required'); // Cho phép ô nhập tên trống khi clone
            filterUniqueNames(); // Gọi hàm lọc tên duy nhất
        } else {
            $('#chooseDecisionDiv').hide();
            $('#rank-name').attr('required', 'required'); // Yêu cầu nhập tên khi không clone
        }
    });

    // Function to filter unique names
    function filterUniqueNames() {
        var uniqueNames = new Set();
        $('#choose-decision option').each(function () {
            var name = $(this).data('name');
            if (uniqueNames.has(name)) {
                $(this).remove();
            } else {
                uniqueNames.add(name);
            }
        });
    }
});