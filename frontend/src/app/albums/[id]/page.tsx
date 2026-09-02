import AlbumEditions from "@/components/AlbumEditions";
import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";

export default async function AlbumPage(props: PageProps<"/albums/[id]">) {
  const { id } = await props.params;

  return (
    <AuthGuard>
      <Header />
      <AlbumEditions id={id} />
    </AuthGuard>
  );
}
