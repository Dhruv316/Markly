export default function Alert({ message }) {
  if (!message) return null;
  return <div className={`alert ${message.type}`}>{message.text}</div>;
}
