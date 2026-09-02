import ArtistDetail from "@/components/ArtistDetail";
import AuthGuard from "@/components/AuthGuard";
import Header from "@/components/Header";

export default async function ArtistPage(props: PageProps<"/artists/[id]">) {
  const { id } = await props.params;

  return (
    <AuthGuard>
      <Header />
      <ArtistDetail id={id} />
    </AuthGuard>
  );
}
