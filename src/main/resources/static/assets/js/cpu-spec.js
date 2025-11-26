// CPU Specification Form AJAX Handler with Edit Support and SweetAlert
$(document).ready(function() {
    // Handle form submission via AJAX
    $('#cpuSpecForm').on('submit', function(e) {
        e.preventDefault();
        
        const form = $(this);
        const submitBtn = $('#submitBtn');
        const submitText = $('#submitText');
        const originalBtnHtml = submitBtn.html();
        
        // Disable button and show loading state
        submitBtn.prop('disabled', true).html('<i class="fa-solid fa-spinner fa-spin"></i> Saving...');
        
        $.ajax({
            url: form.attr('action'),
            type: 'POST',
            data: form.serialize(),
            success: function(response) {
                const isEdit = $('#specId').val() !== '';
                const message = isEdit ? 'CPU Specification updated successfully!' : 'CPU Specification saved successfully!';
                
                // Show success message with SweetAlert
                Swal.fire({
                    title: 'Success!',
                    text: message,
                    icon: 'success',
                    timer: 2000,
                    showConfirmButton: false
                }).then(() => {
                    location.reload();
                });
            },
            error: function(xhr) {
                const errorMsg = xhr.responseText || 'Failed to save specification';
                Swal.fire({
                    title: 'Error!',
                    text: errorMsg,
                    icon: 'error',
                    confirmButtonText: 'OK'
                });
                
                // Re-enable button
                submitBtn.prop('disabled', false).html(originalBtnHtml);
            }
        });
    });
    
    // Handle edit button click
    $('.edit-spec-btn').on('click', function() {
        const specId = $(this).data('id');
        
        // Fetch specification data
        $.ajax({
            url: '/admin/cpu-specs/get/' + specId,
            type: 'GET',
            success: function(spec) {
                // Populate form fields
                $('#specId').val(spec.id);
                $('#manufacturer').val(spec.manufacturer);
                $('#model').val(spec.model);
                $('#processor').val(spec.processor);
                $('#memory').val(spec.memory);
                $('#hardDisk').val(spec.hardDisk);
                $('#purchaseDate').val(spec.purchaseDate || '');
                $('#supplier').val(spec.supplier || '');
                
                // Change button text and icon
                $('#submitBtn').removeClass('btn-primary').addClass('btn-warning');
                $('#submitBtn i').removeClass('fa-plus').addClass('fa-save');
                $('#submitText').text('Update Spec');
                
                // Add cancel button if not exists
                if ($('#cancelEditBtn').length === 0) {
                    const cancelBtn = `
                        <button type="button" id="cancelEditBtn" class="btn btn-secondary">
                            <i class="fa-solid fa-times"></i> Cancel
                        </button>
                    `;
                    $('#submitBtn').parent().append(cancelBtn);
                }
                
                // Scroll to form
                $('html, body').animate({
                    scrollTop: $('#cpuSpecForm').offset().top - 100
                }, 500);
                
                // Focus on first field
                $('#manufacturer').focus();
            },
            error: function() {
                Swal.fire({
                    title: 'Error!',
                    text: 'Failed to load specification data',
                    icon: 'error'
                });
            }
        });
    });
    
    // Handle delete with SweetAlert
    $('.delete-spec-form').on('submit', function(e) {
        e.preventDefault();
        const form = $(this);
        
        Swal.fire({
            title: 'Are you sure?',
            text: "You won't be able to revert this!",
            icon: 'warning',
            showCancelButton: true,
            confirmButtonColor: '#3085d6',
            cancelButtonColor: '#d33',
            confirmButtonText: 'Yes, delete it!'
        }).then((result) => {
            if (result.isConfirmed) {
                form.off('submit').submit();
            }
        });
    });
    
    // Handle cancel edit
    $(document).on('click', '#cancelEditBtn', function() {
        resetForm();
        $(this).remove();
    });
    
    // Reset form function
    function resetForm() {
        $('#cpuSpecForm')[0].reset();
        $('#specId').val('');
        $('#submitBtn').removeClass('btn-warning').addClass('btn-primary');
        $('#submitBtn i').removeClass('fa-save').addClass('fa-plus');
        $('#submitText').text('Add Spec');
    }
});
