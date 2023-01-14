package exceptions;


public final class S3UploadException extends RuntimeException
{
    public S3UploadException(String message)
    {
        super(message);
    }
}
